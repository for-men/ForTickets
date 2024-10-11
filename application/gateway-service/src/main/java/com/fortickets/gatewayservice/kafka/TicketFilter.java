package com.fortickets.gatewayservice.kafka;

import com.fortickets.gatewayservice.kafka.TicketFilter.Config;
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
public class TicketFilter extends AbstractGatewayFilterFactory<Config> {

    @Autowired
    private KafkaTicketIssuer kafkaTicketIssuer;

    @Autowired
    private TrafficMonitor trafficMonitor;

    @Autowired
    private KafkaOffsetManager kafkaOffsetManager;

    public TicketFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String ticketHeader = request.getHeaders().getFirst("X-Ticket-Number");

            // 트래픽 모니터링 시작
            trafficMonitor.incrementRequestCount();
            // 트래픽이 임계치를 초과하면 대기표를 발급
            if (trafficMonitor.isOverloaded()) {
                if (ticketHeader == null) {
                    return kafkaTicketIssuer.issueNewTicketAndRespond(exchange, requestPath);
                }
            }

            // 대기표가 존재할 경우 순서를 확인하여 처리
            if (ticketHeader != null) {
                long currentOffset = kafkaOffsetManager.getCurrentOffsetFromKafka(config.getPartition());
                long ticketNumber = Long.parseLong(ticketHeader);

                if (ticketNumber > currentOffset) {
                    // 대기표의 순서가 아직 도래하지 않은 경우
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS); // 429 상태 반환
                    trafficMonitor.decrementRequestCount();
                    return response.setComplete();
                } else {
                    trafficMonitor.decrementRequestCount();
                }
            }
            // 트래픽이 안정적일 때 요청을 원래의 API로 전달
            return chain.filter(exchange).doFinally(signalType -> trafficMonitor.decrementRequestCount());
        };
    }


    public static class Config {
        // 필요한 구성 설정
        private String topicName = "ticket-queue-topic"; // Kafka 토픽 이름
        private int partition = 0; // 기본 파티션 번호

        // Getter와 Setter 메서드
        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public int getPartition() {
            return partition;
        }

        public void setPartition(int partition) {
            this.partition = partition;
        }
    }
}