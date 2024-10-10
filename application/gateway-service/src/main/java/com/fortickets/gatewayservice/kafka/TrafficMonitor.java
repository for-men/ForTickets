package com.fortickets.gatewayservice.kafka;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * TrafficMonitor는 요청량을 모니터링하여 서버의 상태를 관리하고, 임계치 초과 시 대기열로 전환합니다.
 */
@Slf4j
@Component
public class TrafficMonitor {

    private static final int REQUEST_THRESHOLD = 3; // 예제 임계치 (초당 100개의 요청)
    private final AtomicInteger currentRequestCount = new AtomicInteger(0);

    /**
     * API 요청 수를 증가시킵니다.
     */
    public void incrementRequestCount() {
        int currentCount = currentRequestCount.incrementAndGet();
        log.info("Current request count incremented: {}", currentCount);
    }

    /**
     * API 요청 처리가 완료되면 요청 수를 감소시킵니다.
     */
    public void decrementRequestCount() {
        int currentCount = currentRequestCount.decrementAndGet();
        log.info("Current request count decremented: {}", currentCount);
    }

    /**
     * 현재 요청량이 임계치를 초과하는지 확인합니다.
     *
     * @return true이면 임계치를 초과함을 의미합니다.
     */
    public boolean isOverloaded() {
        boolean overloaded = currentRequestCount.get() > REQUEST_THRESHOLD;
        log.info("Is overloaded: {}", overloaded);
        return overloaded;
    }
}