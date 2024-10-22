package com.fortickets.orderservice.infrastructure.scheduler;

import com.fortickets.common.util.BookingStatus;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final BookingRepository bookingRepository;

    // 10분마다 한번씩 실행
    @Scheduled(cron = "0 0/10 * * * * ")
    @Transactional
    @Async
    public void cancelBooking() {

        LocalDateTime deleteTime = LocalDateTime.now().minusMinutes(5);

        bookingRepository.findAllByStatusAndCreatedAtBefore(BookingStatus.PENDING, deleteTime)
            .forEach(booking -> {
                booking.delete("system");
            });

    }

}
