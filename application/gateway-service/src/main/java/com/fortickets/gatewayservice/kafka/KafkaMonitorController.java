package com.fortickets.gatewayservice.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/monitor")
@AllArgsConstructor
public class KafkaMonitorController {

    private final KafkaMonitor kafkaMonitor;

    private final KafkaConsumerManager kafkaConsumerManager;


    // 현재 요청수 확인
    @GetMapping("/current-count")
    public int getCurrentRequestCount() {
        return kafkaMonitor.getCurrentRequestCount();
    }

    // 요청 수를 0으로 초기화하는 API 추가
    @GetMapping("/reset-count")
    public String resetRequestCount() {
        kafkaMonitor.resetRequestCount(); // 요청 수 초기화
        return "Request count has been reset to 0.";
    }

    // 대기표 남은 순번 확인
    @GetMapping("/waiting-count/{uuid}")
    public String getWaitingCount(@PathVariable("uuid") String uuid) {
        // 1. UUID에 해당하는 offset을 가져옵니다.
        Long offset = kafkaMonitor.getWaitingTickets().get(uuid); // 대기자 큐에서 UUID에 해당하는 offset 가져오기

        if (offset == null) {
            return "UUID not found in the waiting queue."; // 대기자 목록에 UUID가 없다면
        }

        // 2. 현재 Kafka의 마지막 소비된 offset을 가져옵니다.
        long currentOffset = kafkaConsumerManager.getLastCommittedOffset(0); // 0번 파티션의 현재 오프셋 가져오기

        // 3. 대기자 수 계산: 입력된 UUID의 offset과 현재 Kafka의 offset 비교
        int waitingCount = (int) (offset - currentOffset); // 음수 방지: 현재 오프셋이 더 클 수 있기 때문에 음수 처리

        // 4. 대기자 수가 0보다 작으면 0으로 설정
        waitingCount = Math.max(waitingCount, 0); // 대기자 수가 0 미만일 경우 0으로 설정

        // 5. 대기자 수가 0이 아니면 남은 대기자 수 출력
        return "남은 대기자 수는: " + waitingCount + " 명 입니다.";
    }

    // 소비중인 offset 확인
    @GetMapping("/currentOffset")
    public String currentOffset() {
        long consumerOffset = kafkaConsumerManager.getLastCommittedOffset(0);
        return "현재 소비된 offset은" + consumerOffset;
    }

    // 입력한 uuid의 offset 확인
    @GetMapping("/uuidOffset/{uuid}")
    public String uuidOffset(@PathVariable("uuid") String uuid) {
        Long uuidOffset = kafkaMonitor.getWaitingTickets().get(uuid);
        return "이 uuid의 offset은" + uuidOffset;
    }
}
