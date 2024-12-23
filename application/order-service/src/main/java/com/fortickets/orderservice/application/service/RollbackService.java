package com.fortickets.orderservice.application.service;

import com.fortickets.orderservice.application.context.BookingRollbackContext;
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
    public void decrementSeatsRollback(BookingRollbackContext rollbackContext) {
        try {
            kafkaTemplate.send("concert.increment-seats", rollbackContext.getDecrementSeatsRollbackReq());
        } catch (Exception e) {
            log.error("[RollbackService] decrementSeatsRollback error : {}", e.getMessage());
        }
    }

    // 예매 생성 실패 - 예매 삭제
    public void createBookingRollback(BookingRollbackContext rollbackContext) {
        try {
            kafkaTemplate.send("order.delete-bookings", rollbackContext.getDeleteBookingsRollbackReq());
        } catch (Exception e) {
            log.error("[RollbackService] createBookingRollback error : {}", e.getMessage());
        }
    }
}
