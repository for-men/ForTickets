package com.fortickets.orderservice.infrastructure.scheduler;

import com.fortickets.common.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final BookingRepository bookingRepository;

    // 1분에 한번씩 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    @Async
    public void cancelBooking() {

        LocalDateTime deleteTime = LocalDateTime.now().minusMinutes(5);

        List<Booking> bookingList = bookingRepository.findAllByStatusAndCreatedAtBefore(BookingStatus.PENDING, deleteTime).stream()
            .map(booking -> {
                booking.delete("system");
                return booking;
            }).toList();

    }

}
