package com.fortickets.gatewayservice.newkafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID; // UUID 추가
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * KafkaProducer는 클라이언트 요청을 Kafka로 발행하는 클래스입니다.
 */
@Component
@Slf4j
public class KafkaProducerManager {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "ticket-queue-topic"; // 사용할 Kafka 토픽 이름
    private final KafkaMonitor kafkaMonitor;
    private final ObjectMapper objectMapper; // JSON 직렬화를 위한 ObjectMapper

    public KafkaProducerManager(KafkaTemplate<String, String> kafkaTemplate, KafkaMonitor kafkaMonitor) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMonitor = kafkaMonitor;
        this.objectMapper = new ObjectMapper(); // ObjectMapper 초기화
    }

    public Mono<Void> sendTicket(ServerWebExchange exchange) {
        // 요청 정보 수집
        String requestPath = exchange.getRequest().getURI().toString();
        String headers = exchange.getRequest().getHeaders().toString(); // 요청 Headers 가져오기

        // 요청 본문 읽기
        Mono<String> requestBodyMono = exchange.getRequest().getBody()
            .flatMap(dataBuffer -> Mono.just(dataBuffer.toString(StandardCharsets.UTF_8)))
            .collectList()
            .map(list -> String.join("", list)); // 리스트를 문자열로 합치기

        return requestBodyMono.flatMap(requestBody -> {
            try {
                // 요청 정보를 RequestData 객체로 변환
                RequestData requestData = new RequestData(requestPath, headers, requestBody);

                // RequestData 객체를 JSON 문자열로 직렬화
                String jsonMessage = objectMapper.writeValueAsString(requestData);

                // Kafka에 메시지 전송
                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, jsonMessage);

                return Mono.fromFuture(future)
                    .flatMap(sendResult -> {
                        RecordMetadata metadata = sendResult.getRecordMetadata();
                        long offset = metadata.offset(); // Kafka Offset을 대기표로 사용
                        ServerHttpResponse response = exchange.getResponse();

                        // 대기표 상태를 저장
                        String randomUUID = UUID.randomUUID().toString();
                        response.getHeaders().add("X-Ticket-Number", String.valueOf(offset));
                        response.getHeaders().add("X-Random-UUID", randomUUID); // UUID 추가
                        kafkaMonitor.addWaitingTicket(randomUUID, offset, exchange); // 대기표 추가

                        response.setStatusCode(HttpStatus.ACCEPTED); // 202 상태 반환
                        log.info("Ticket issued: {}, with Kafka Offset: {}", randomUUID, offset);
                        return response.setComplete();
                    })
                    .doOnError(error -> log.error("Error issuing ticket to Kafka: {}", error.getMessage()));
            } catch (Exception e) {
                log.error("Failed to serialize message: {}", e.getMessage());
                return Mono.error(e); // 예외 처리
            }
        });
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class RequestData {
        private String url;
        private String headers;
        private String body;
    }
}