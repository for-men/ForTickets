package com.fortickets.gatewayservice.newkafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class KafkaConsumerManager implements ConsumerSeekAware {

    private final ConsumerFactory<String, String> consumerFactory;
    private final String topic = "ticket-queue-topic"; // Kafka 토픽 이름
    private final ExecutorService executorService;
    private final int POOL_SIZE = 10; // 소비자 풀의 크기
    private final WebClient webClient; // WebClient 주입을 위한 필드 추가
    private final ObjectMapper objectMapper; // Jackson ObjectMapper 주입
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수

    @Autowired
    public KafkaConsumerManager(ConsumerFactory<String, String> consumerFactory, WebClient.Builder webClientBuilder) {
        this.consumerFactory = consumerFactory;
        this.executorService = Executors.newFixedThreadPool(POOL_SIZE);
        this.webClient = webClientBuilder.build(); // WebClient 초기화
        this.objectMapper = new ObjectMapper(); // ObjectMapper 초기화
    }

    // Kafka 메시지 수신 (스프링 관리)
    @KafkaListener(topics = "ticket-queue-topic", groupId = "ticket-consumer-group")
    public void listen(String message) {
        log.info("Received message: {}", message);
        boolean success = handleMessage(message);

        if (!success) {
            handleRetry(message, 1);
        }
    }

    // 메시지 처리 로직
    private boolean handleMessage(String message) {
        try {
            log.info("Processing message: {}", message);

            // 메시지 파싱: URL, 헤더, 바디 정보를 추출
            RequestData requestData = objectMapper.readValue(message, RequestData.class);
            log.info("Parsed RequestData: {}", requestData);

            // 재전송 로직: HTTP 요청 보내기
            String response = resendRequest(requestData);
            log.info("Request resent with response: {}", response);

            return true; // 성공적으로 처리했을 경우
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            return false; // 처리 실패
        }
    }

    // 재전송을 위한 HTTP 요청 메서드
    private String resendRequest(RequestData requestData) {
        return webClient.post()
            .uri(requestData.getUrl())
            .headers(httpHeaders -> {
                httpHeaders.set("X-Resend-Request", "true");
                // 헤더 정보 추가 (안전한 파싱)
                String[] headerPairs = requestData.getHeaders()
                    .replaceAll("[\\[\\]\"]", "") // 대괄호와 따옴표 제거
                    .split(", ");
                for (String header : headerPairs) {
                    // "accept: application/json" 형식으로 분리
                    String[] keyValue = header.split(":\\s*", 2); // 콜론과 공백으로 분리
                    if (keyValue.length == 2) { // 유효한 키-값인지 체크
                        httpHeaders.set(keyValue[0].trim(), keyValue[1].trim());
                    } else {
                        log.warn("Skipping invalid header: {}", header);
                    }
                }
            })
            .bodyValue(requestData.getBody())
            .retrieve()
            .bodyToMono(String.class)
            .block(); // 동기적으로 응답을 받기 위해 block() 사용
    }

    // 리트라이 로직
    private void handleRetry(String message, int attempt) {
        if (attempt > MAX_RETRIES) {
            log.error("Max retry attempts reached for message: {}", message);
            return;
        }

        log.info("Retrying message: {} (Attempt {}/{})", message, attempt, MAX_RETRIES);
        boolean success = handleMessage(message);
        if (!success) {
            handleRetry(message, attempt + 1); // 재시도
        }
    }

    public long getCurrentOffsetFromKafka(int partition) {
        Future<Long> future = executorService.submit(() -> {
            try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
                TopicPartition topicPartition = new TopicPartition(topic, partition);
                consumer.assign(Collections.singletonList(topicPartition));
                consumer.seekToEnd(Collections.singletonList(topicPartition));
                return consumer.position(topicPartition);
            }
        });

        try {
            return future.get(); // Future의 결과를 기다림
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리
            return -1; // 실패 시 적절한 값을 반환
        }
    }

    // 필요 시 특정 위치로 오프셋 조정
    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        log.info("Seek callback registered.");
    }

    // ExecutorService 종료 메서드
    public void shutdown() {
        executorService.shutdown();
    }

    // 요청 데이터 저장을 위한 내부 클래스
    @Getter
    public static class RequestData {
        private String url;
        private String headers;
        private String body;

        public RequestData() { }
    }
}