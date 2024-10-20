package com.fortickets.orderservice.infrastructure.messaging;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.BookingStatus;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingCancelConsumer {

//    @KafkaListener(topics = "booking-cancel-topic", groupId = "booking-group")
//    public void consume(String message) {
//        log.info("5. Kafka에서 메시지를 수신했습니다: {}", message);
//
//        // 메시지를 기반으로 비동기 작업 수행 추가(예: 알림 발송, 데이터베이스 상태 업데이트 등)
//
//    }
//
//    private void processBookingCancel(String message) {
//        // 메시지 처리 로직 구현
//        log.info("Processing booking cancel: {}", message);
//    }

    // ============== 테스트용 =================
    private final BookingRepository bookingRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BookingCancelConsumer(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @KafkaListener(topics = "booking-cancel-topic", groupId = "booking-group")
    public void consume(String message) {
        log.info("5. Kafka에서 메시지를 수신했습니다.: {}", message);

        // 10초 후에 상태 변경 작업 수행
        log.info("6. 수신된 메시지를 기반으로 예약 취소 작업을 5초 후에 작동합니다. message: {}", message);
        scheduler.schedule(() -> processBookingCancel(message), 5   , TimeUnit.SECONDS);
    }

    private void processBookingCancel(String message) {
        // 메시지에서 bookingId 추출
        Long bookingId = extractBookingIdFromMessage(message);

        // Booking 객체를 찾아 상태 변경
        log.info("7. 예약 취소 작업을 처리 중입니다. Booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));

        booking.setStatus(BookingStatus.CANCELED);  // 상태 변경
        bookingRepository.save(booking);

        log.info("8. 예매 상태가 CANCELED로 변경되었습니다. Booking ID: {}", bookingId);
    }

    private Long extractBookingIdFromMessage(String message) {
        // 메시지에서 bookingId를 추출하는 로직 구현
        // 예시: "Booking canceled for ID: 1" -> 1을 추출
        return Long.parseLong(message.split(": ")[1]);
    }

}
