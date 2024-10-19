package com.fortickets.gatewayservice.newkafka;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerManager implements ConsumerSeekAware {

    private final ConsumerFactory<String, String> consumerFactory;
    private final String topic = "ticket-queue-topic"; // Kafka 토픽 이름
    private final ExecutorService executorService;
    private final int POOL_SIZE = 10; // 소비자 풀의 크기

    @Autowired
    public KafkaConsumerManager(ConsumerFactory<String, String> consumerFactory) {
        this.consumerFactory = consumerFactory;
        this.executorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    // Kafka 메시지 수신 (스프링 관리)
    @KafkaListener(topics = "ticket-queue-topic", groupId = "ticket-consumer-group")
    public void listen(String message) {
        log.info("Received message: {}", message);
        handleMessage(message);
        //리트라이 로직 추가 (임계치 기준으로 작동하도록)
        // 카프카가 아닌 queeu에 대기열을 쌓도록 설정
    }

    // 메시지 처리 로직
    private void handleMessage(String message) {
        log.info("Processing message: {}", message);
        // TODO: 비즈니스 로직 수행
    }

    public long getCurrentOffsetFromKafka(int partition) {
        // ExecutorService를 사용하여 소비자 작업을 비동기적으로 수행
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
}