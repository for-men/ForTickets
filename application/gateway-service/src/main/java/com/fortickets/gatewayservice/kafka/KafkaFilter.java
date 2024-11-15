package com.fortickets.gatewayservice.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaFilter extends AbstractGatewayFilterFactory<KafkaFilter.Config> {

    private final KafkaProducerManager kafkaProducer;
    private final KafkaMonitor kafkaMonitor;
    private final KafkaConsumerManager kafkaConsumerManager;

    @Autowired
    public KafkaFilter(KafkaProducerManager kafkaProducer, KafkaMonitor kafkaMonitor,
        KafkaConsumerManager kafkaConsumerManager) {
        super(Config.class);
        this.kafkaProducer = kafkaProducer;
        this.kafkaMonitor = kafkaMonitor;
        this.kafkaConsumerManager = kafkaConsumerManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String ticketHeader = request.getHeaders().getFirst("X-Ticket-Number");
            String resendHeader = request.getHeaders().getFirst("X-Resend-Request"); // 재전송 요

            // 트래픽 모니터링 시작
            kafkaMonitor.incrementRequestCount();

            // 재전송 요청인 경우
            if (resendHeader != null) {
                log.info("Processing resend request with X-Resend-Request header: {}", resendHeader);
                // 재전송된 요청을 처리
                return chain.filter(exchange)
                    .doFinally(signalType -> {
                        kafkaMonitor.decrementRequestCount();
                        log.info("Request count decremented after processing resend request.");
                    });
            }
            // 트래픽이 임계치를 초과할 경우
            if (kafkaMonitor.isOverloaded()) {
                // 대기표가 없으면 메시지를 발급
                if (ticketHeader == null) {
                    return kafkaProducer.sendTicket(exchange) // 모든 정보가 포함된 메시지 전송
                        .doOnSuccess(aVoid -> {
                            log.info("Ticket issued successfully.");
                        });
                } else {
                    // 대기표가 있을 경우, 현재 오프셋을 확인하고 처리
                    long currentOffset = kafkaConsumerManager.getCurrentOffsetFromKafka(config.getPartition());
                    long ticketNumber = Long.parseLong(ticketHeader);

                    if (ticketNumber > currentOffset) {
                        // 대기표의 순서가 아직 도래하지 않은 경우
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.OK); // 요청을 대기 상태로 설정
                        kafkaMonitor.decrementRequestCount();
                        return response.setComplete(); // 요청을 종료
                    }
                }
            }

            // 트래픽이 안정적일 때 요청을 원래의 API로 전달
            return chain.filter(exchange).doFinally(signalType -> kafkaMonitor.decrementRequestCount());
        };
    }

    @Getter
    public static class Config {
        private String topicName = "ticket-queue-topic";
        private int partition = 0;
    }
}