package com.fortickets.gatewayservice.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * KafkaMonitor는 요청량을 모니터링하여 서버의 상태를 관리하고, 임계치 초과 시 대기열로 전환합니다.
 */
@Slf4j
@Component
public class KafkaMonitor {

    private static final int REQUEST_THRESHOLD = 1; //임계치
    private final AtomicInteger currentRequestCount = new AtomicInteger(0);
    private final AtomicInteger failedRequestCount = new AtomicInteger(0);
    private final Map<String, Long> waitingTickets = new ConcurrentHashMap<>(); // UUID와 offset 매핑
    private final Map<String, ServerWebExchange> exchangeMap = new ConcurrentHashMap<>(); // UUID와 exchange 매핑
    private boolean wasOverloaded = false;
    private final KafkaAdmin kafkaAdmin;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 스케줄러 생성

    public KafkaMonitor(KafkaAdmin kafkaAdmin){
        this.kafkaAdmin = kafkaAdmin;
    };

    // 요청 수 증가
    public synchronized void incrementRequestCount() {
        int currentCount = currentRequestCount.incrementAndGet();
        log.info("Request count incremented, current count: {}", currentCount);
        checkOverloaded();
    }

    // 요청 수 감소
    public synchronized void decrementRequestCount() {
        int currentCount = currentRequestCount.decrementAndGet();
        log.info("Request count decremented, current count: {}", currentCount);
        checkOverloaded();
    }

    // 최종 실패한 요청 수 증가
    public synchronized void incrementFailedRequestCount() {
        int failedCount = failedRequestCount.incrementAndGet();
        decrementRequestCount();
        log.info("Failed request count incremented, current failed count: {}", failedCount);
    }

    // 과부하 체크
    private void checkOverloaded() {
        int currentCount = currentRequestCount.get();
        boolean overloaded = currentCount > REQUEST_THRESHOLD;

        // 상태 변화가 있을 경우에만 로그 출력
        if (overloaded != wasOverloaded) {
            log.info("Is overloaded: {}", overloaded);
            wasOverloaded = overloaded; // 현재 상태로 업데이트

            // 과부하가 발생했을 경우 대기표 상태 확인
            if (overloaded) {
                checkWaitingTickets(); // 대기표 상태 확인
            }
        }
    }

    // 과부하 상태 반환
    public boolean isOverloaded() {
        return currentRequestCount.get() > REQUEST_THRESHOLD;
    }

    // 대기표 추가
    public void addWaitingTicket(String uuid, long offset, ServerWebExchange exchange) {
        waitingTickets.put(uuid, offset);
        exchangeMap.put(uuid, exchange); // 대기표 추가 시 exchange도 함께 저장
    }

    // 대기표 상태 확인
    private void checkWaitingTickets() {
        waitingTickets.forEach((uuid, offset) -> {
            long currentOffset = getCurrentOffsetFromKafka(0); // 파티션 0의 현재 오프셋 확인
            if (offset <= currentOffset) {
                // 사용자의 차례가 되었을 때 처리
                // 3초 후에 대기표에서 삭제되도록 예약
                scheduler.schedule(() -> {
                    // 일정 시간이 지난 후 대기표에서 삭제
                    waitingTickets.remove(uuid);
                    exchangeMap.remove(uuid); // exchange도 함께 제거
                    log.info("UUID {} removed from waiting tickets after delay", uuid);
                }, 3, TimeUnit.SECONDS); // 3초 후 삭제
            }
        });

        log.info("Current request count after processing: {}", currentRequestCount.get());
        // 과부하 상태 체크
        checkOverloaded(); // 현재 요청 수로 과부하 상태 체크
    }


    // 현재 요청 수를 반환하는 메서드
    public int getCurrentRequestCount() {
        return currentRequestCount.get();
    }

    // KafkaAdmin을 사용하여 오프셋을 가져오는 방식으로 변경
    public long getCurrentOffsetFromKafka(int partition) {
        try {
            // KafkaAdmin을 통해 AdminClient 생성
            Map<String, Object> config = kafkaAdmin.getConfigurationProperties();
            AdminClient adminClient = AdminClient.create(config);

            // TopicPartition 정의
            TopicPartition topicPartition = new TopicPartition("ticket-queue-topic", partition);

            // ListOffsetsResult로 오프셋 요청
            ListOffsetsResult offsetsResult = adminClient.listOffsets(
                Map.of(topicPartition, OffsetSpec.latest())  // 최신 오프셋을 요청
            );

            // 오프셋을 가져오는 방식: all() 메서드 호출 후 해당 파티션의 오프셋을 조회
            long currentOffset = offsetsResult.all().get().get(topicPartition).offset();

            return currentOffset;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while getting current offset: {}", e.getMessage());
            return -1; // 실패 시 -1 반환
        }
    }

    // 요청 수를 0으로 초기화하는 메서드 추가
    public void resetRequestCount() {
        currentRequestCount.set(0); // AtomicInteger의 값을 0으로 초기화
        log.info("Request count reset to 0");
    }
    // waitingTickets 반환
    public Map<String, Long> getWaitingTickets() {
        return waitingTickets;
    }
}
