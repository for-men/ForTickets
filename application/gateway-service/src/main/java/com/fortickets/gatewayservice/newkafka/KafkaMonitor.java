//package com.fortickets.gatewayservice.newkafka;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * KafkaMonitor는 요청량을 모니터링하여 서버의 상태를 관리하고, 임계치 초과 시 대기열로 전환합니다.
// */
//@Slf4j
//@Component
//public class KafkaMonitor {
//
//    private static final int REQUEST_THRESHOLD = 10; // 예제 임계치
//    private final AtomicInteger currentRequestCount = new AtomicInteger(0);
//    private final Map<String, Long> waitingTickets = new ConcurrentHashMap<>(); // UUID와 offset 매핑
//    private final Map<String, ServerWebExchange> exchangeMap = new ConcurrentHashMap<>(); // UUID와 exchange 매핑
//    private boolean wasOverloaded = false; // 이전 과부하 상태
//    private final KafkaConsumerManager consumerManager; // KafkaConsumerManager 객체 추가
//
//    // 생성자 주입
//    public KafkaMonitor(KafkaConsumerManager consumerManager) {
//        this.consumerManager = consumerManager;
//    }
//
//    // 요청 수 증가
//    public void incrementRequestCount() {
//        currentRequestCount.incrementAndGet();
//        checkOverloaded(); // 과부하 체크
//    }
//
//    // 요청 수 감소
//    public void decrementRequestCount() {
//        currentRequestCount.decrementAndGet();
//        checkOverloaded(); // 과부하 체크
//    }
//
//    // 과부하 체크
//    private void checkOverloaded() {
//        boolean overloaded = currentRequestCount.get() > REQUEST_THRESHOLD;
//
//        // 상태 변화가 있을 경우에만 로그 출력
//        if (overloaded != wasOverloaded) {
//            log.info("Is overloaded: {}", overloaded);
//            wasOverloaded = overloaded; // 현재 상태로 업데이트
//
//            // 과부하가 발생했을 경우 대기표 상태 확인
//            if (overloaded) {
//                checkWaitingTickets(); // 대기표 상태 확인
//            }
//        }
//    }
//
//    // 과부하 상태 반환
//    public boolean isOverloaded() {
//        return currentRequestCount.get() > REQUEST_THRESHOLD;
//    }
//
//    // 대기표 추가
//    public void addWaitingTicket(String uuid, long offset, ServerWebExchange exchange) {
//        waitingTickets.put(uuid, offset);
//        exchangeMap.put(uuid, exchange); // 대기표 추가 시 exchange도 함께 저장
//    }
//
//    // 대기표 상태 확인
//    private void checkWaitingTickets() {
//        waitingTickets.forEach((uuid, offset) -> {
//            long currentOffset = consumerManager.getCurrentOffsetFromKafka(0); // 예: 파티션 0의 현재 오프셋 확인
//            if (offset <= currentOffset) {
//                // 사용자의 차례가 되었을 때 재요청
////                resendRequest(uuid);
//                waitingTickets.remove(uuid);
//                exchangeMap.remove(uuid); // 대기표에서 제거할 때 exchange도 함께 제거
//            }
//        });
//    }
//
////    // 요청 재전송 로직
////    private void resendRequest(String uuid) {
////        // UUID에 해당하는 사용자의 요청을 재전송하는 로직 구현
////        log.info("Resending request for ticket UUID: {}", uuid);
////
////        // UUID에 해당하는 대기표의 오프셋 가져오기
////        Long offset = waitingTickets.get(uuid);
////        if (offset != null) {
////            // WebClient를 사용하여 요청 재전송
////            // 재전송할 요청의 정보가 필요
////            ServerWebExchange exchange = exchangeMap.get(uuid);
////
////            if (exchange != null) {
////                String requestPath = exchange.getRequest().getPath().toString();
////                HttpMethod method = exchange.getRequest().getMethod();
////
////                WebClient webClient = WebClient.create();
////                Mono<Void> response = webClient.method(method)
////                    .uri(requestPath)
////                    .retrieve()
////                    .bodyToMono(Void.class);
////
////                response.doOnSuccess(aVoid -> log.info("Successfully resent request for ticket UUID: {}", uuid))
////                    .doOnError(error -> log.error("Error resending request for ticket UUID: {}", uuid, error))
////                    .subscribe();
////            } else {
////                log.warn("No request found for ticket UUID: {}", uuid);
////            }
////        } else {
////            log.warn("No offset found for ticket UUID: {}", uuid);
////        }
////    }
//}