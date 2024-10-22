package com.fortickets.orderservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        log.info("2. Kafka로 메시지를 전송 중입니다. Topic: {}, Message: {}", topic, message);
        kafkaTemplate.send(topic, message);
        kafkaTemplate.flush();
        log.info("3. Kafka로 메시지가 성공적으로 전송되었습니다. Topic: {}, Message: {}", topic, message);

    }

}
