package com.fortickets.gatewayservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * KafkaTicketIssuer는 클라이언트 요청을 Kafka로 발행하고, 메시지 오프셋을 대기표 번호로 사용하는 클래스입니다.
 */
@Component
@Slf4j
public class KafkaTicketIssuer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "ticket-queue-topic"; // 사용할 Kafka 토픽 이름

    public KafkaTicketIssuer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 새로운 대기표를 발급하고 Kafka로 메시지를 발행합니다.
     *
     * @return 대기표 번호
     */
    public String issueNewTicket(String url) {
        String ticketId = UUID.randomUUID().toString(); // 고유한 대기표 ID 생성
        kafkaTemplate.send(topic, ticketId, url);
        return ticketId;
    }

    public Mono<Void> issueNewTicketAndRespond(ServerWebExchange exchange, String requestPath) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, requestPath);

        return Mono.fromFuture(future)
            .flatMap(sendResult -> {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                long offset = metadata.offset(); // Kafka Offset을 대기표로 사용
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().add("X-Ticket-Number", String.valueOf(offset));
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS); // 429 상태 반환
                return response.setComplete();
            })
            .doOnError(error -> log.error("Error issuing ticket to Kafka: {}", error.getMessage()));
    }
}