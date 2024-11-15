package com.fortickets.gatewayservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    private final String topic = "ticket-queue-topic";
    private final KafkaMonitor kafkaMonitor;
    private final ObjectMapper objectMapper; // JSON 직렬화를 위한 ObjectMapper

    public KafkaProducerManager(KafkaTemplate<String, String> kafkaTemplate, KafkaMonitor kafkaMonitor, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMonitor = kafkaMonitor;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> sendTicket(ServerWebExchange exchange) {
        String requestMethod = exchange.getRequest().getMethod().name(); // HTTP 메서드 가져오기
        String requestPath = exchange.getRequest().getURI().toString();
        String headers = exchange.getRequest().getHeaders().toString();

        Mono<String> requestBodyMono = exchange.getRequest().getBody()
            .flatMap(dataBuffer -> Mono.just(dataBuffer.toString(StandardCharsets.UTF_8)))
            .collectList()
            .map(list -> String.join("", list));

        return requestBodyMono.flatMap(requestBody -> {
            try {
                //UUID
                String randomUUID = UUID.randomUUID().toString();

                // RequestData 객체에 메서드 추가
                RequestData requestData = new RequestData(requestMethod, requestPath, headers, requestBody, randomUUID);

                String jsonMessage = objectMapper.writeValueAsString(requestData);

                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, jsonMessage);

                return Mono.fromFuture(future)
                    .flatMap(sendResult -> {
                        RecordMetadata metadata = sendResult.getRecordMetadata();
                        long offset = metadata.offset();
                        ServerHttpResponse response = exchange.getResponse();

                        response.getHeaders().add("X-Ticket-Number", String.valueOf(offset));
                        response.getHeaders().add("X-Random-UUID", randomUUID);
                        kafkaMonitor.addWaitingTicket(randomUUID, offset, exchange);

                        response.setStatusCode(HttpStatus.ACCEPTED);
                        log.info("Ticket issued: {}, with Kafka Offset: {}", randomUUID, offset);
                        return response.setComplete();
                    })
                    .doOnError(error -> log.error("Error issuing ticket to Kafka: {}", error.getMessage()));
            } catch (Exception e) {
                log.error("Failed to serialize message: {}", e.getMessage());
                return Mono.error(e);
            }
        });
    }
}
