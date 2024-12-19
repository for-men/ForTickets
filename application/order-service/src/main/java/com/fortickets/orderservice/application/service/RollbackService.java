package com.fortickets.orderservice.application.service;

import com.fortickets.orderservice.application.dto.request.DecrementScheduleRollbackReq;
import com.fortickets.orderservice.application.dto.response.DecrementScheduleRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RollbackService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    // 예매 생성 실패 - 좌석 수 복구
    public void decrementSeatsRollback(Integer quantity, Long scheduleId) {
        try {
            DecrementScheduleRollbackReq decrementScheduleRollbackReq = new DecrementScheduleRollbackReq(quantity, scheduleId);
            kafkaTemplate.send("concert.increment-seats", decrementScheduleRollbackReq);
        } catch (Exception e) {
            log.error("[RollbackService] decrementSeatsRollback error : {}", e.getMessage());
        }
    }

    // 예매 생성 실패 - 예매 삭제
    public void createBookingRollback() {
        try {
            kafkaTemplate.send("booking.delete", );
        } catch (Exception e) {
            log.error("[RollbackService] createBookingRollback error : {}", e.getMessage());
        }
    }
}
