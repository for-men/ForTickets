package com.fortickets.gatewayservice.newkafka;

import com.fortickets.gatewayservice.newkafka.KafkaFilter.Config;
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
public class KafkaFilter extends AbstractGatewayFilterFactory<Config> {

    private final KafkaProducerManager kafkaProducer;
    private final KafkaMonitor kafkaMonitor;
    private final KafkaConsumerManager kafkaConsumerManager;
    private final KafkaSchedule kafkaSchedule;

    @Autowired
    public KafkaFilter(KafkaProducerManager kafkaProducer, KafkaMonitor kafkaMonitor,
        KafkaConsumerManager kafkaConsumerManager, KafkaSchedule kafkaSchedule) {
        super(Config.class);
        this.kafkaProducer = kafkaProducer;
        this.kafkaMonitor = kafkaMonitor;
        this.kafkaConsumerManager = kafkaConsumerManager;
        this.kafkaSchedule = kafkaSchedule; // 추가
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String ticketHeader = request.getHeaders().getFirst("X-Ticket-Number");

            // 트래픽 모니터링 시작
            kafkaMonitor.incrementRequestCount();
            // 트래픽이 임계치를 초과하면 대기표를 발급
            if (kafkaMonitor.isOverloaded()) {
                if (ticketHeader == null) {
                    return kafkaProducer.sendTicket(exchange, requestPath)
                        .doOnSuccess(aVoid -> {
                            // 대기표 발급 후 KafkaSchedule에 대기표 추가
                            Long offset = Long.parseLong(exchange.getResponse().getHeaders().getFirst("X-Ticket-Number"));
                            kafkaSchedule.addWaitingTicket(offset, exchange); // exchange 객체 전달
                        });
                }
            }

            // 대기표가 존재할 경우 순서를 확인하여 처리
            if (ticketHeader != null) {
                long currentOffset = kafkaConsumerManager.getCurrentOffsetFromKafka(config.getPartition());
                long ticketNumber = Long.parseLong(ticketHeader);

                if (ticketNumber > currentOffset) {
                    // 대기표의 순서가 아직 도래하지 않은 경우
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.OK); // 429 상태 반환
                    kafkaMonitor.decrementRequestCount();
                    return response.setComplete();
                } else {
                    kafkaMonitor.decrementRequestCount();
                }
            }
            // 트래픽이 안정적일 때 요청을 원래의 API로 전달
            return chain.filter(exchange).doFinally(signalType -> kafkaMonitor.decrementRequestCount());
        };
    }

    @Getter
    public static class Config {
        private String topicName = "ticket-queue-topic"; // Kafka 토픽 이름
        private int partition = 0; // 기본 파티션 번호
    }
}
