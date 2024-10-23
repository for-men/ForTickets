package com.fortickets.orderservice.infrastructure.messaging;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.orderservice.application.service.PaymentService;
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

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BookingCancelConsumer(BookingRepository bookingRepository, PaymentService paymentService) {
        this.bookingRepository = bookingRepository;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "booking-cancel-topic", groupId = "booking-group")
    public void consume(String message) {
        log.info("5. Kafka에서 메시지를 수신했습니다.: {}", message);

        // 10초 후에 상태 변경 작업 수행
        log.info("6. 수신된 메시지를 기반으로 예약 취소 작업을 10초 후에 작동합니다. message: {}", message);
        scheduler.schedule(() -> processBookingCancel(message), 10, TimeUnit.SECONDS);
    }

    private void processBookingCancel(String message) {
        // 메시지에서 bookingId 추출
        Long bookingId = extractBookingIdFromMessage(message);

        // Booking 객체를 찾아 상태 변경
        log.info("7. 예약 취소 작업을 처리 중입니다. Booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));

        paymentService.cancelPaymentTest(booking.getPayment().getId()); // 예매, 결제 상태 변경

        log.info("8. 예매 상태가 CANCELED로 변경되었습니다. Booking ID: {}", bookingId);
    }

    private Long extractBookingIdFromMessage(String message) {
        // 메시지에서 bookingId를 추출하는 로직 구현
        // 예시: "Booking canceled for ID: 1" -> 1을 추출
        return Long.parseLong(message.split(": ")[1]);
    }

}
