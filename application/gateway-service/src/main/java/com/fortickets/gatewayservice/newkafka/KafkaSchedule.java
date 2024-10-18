package com.fortickets.gatewayservice.newkafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class KafkaSchedule {

    private final KafkaProducerManager kafkaProducer;
    private final KafkaConsumerManager kafkaConsumerManager;

    // 대기표를 추적하기 위한 ConcurrentHashMap (티켓 번호와 요청 정보)
    private final Map<Long, ServerWebExchange> waitingTickets = new ConcurrentHashMap<>();

    @Autowired
    public KafkaSchedule(KafkaProducerManager kafkaProducer, KafkaConsumerManager kafkaConsumerManager) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumerManager = kafkaConsumerManager;
    }

    // 대기표 확인 및 재전송 스케줄
    @Scheduled(fixedRate = 3000) // 3초마다 실행
    public void checkWaitingTickets() {
        for (Map.Entry<Long, ServerWebExchange> entry : waitingTickets.entrySet()) {
            Long ticketNumber = entry.getKey();
            ServerWebExchange exchange = entry.getValue();

            long currentOffset = kafkaConsumerManager.getCurrentOffsetFromKafka(0); // 기본 파티션 사용

            if (ticketNumber <= currentOffset) {
                // 현재 오프셋이 대기표 번호보다 크거나 같으면 요청 재전송
                kafkaProducer.sendTicket(exchange, exchange.getRequest().getPath().toString()); // 요청 재전송
                waitingTickets.remove(ticketNumber); // 대기표에서 제거
            }
        }
    }

    // 대기표를 추가하는 메서드
    public void addWaitingTicket(Long ticketNumber, ServerWebExchange exchange) {
        waitingTickets.put(ticketNumber, exchange);
    }
}
