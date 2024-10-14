package com.fortickets.orderservice.domain.repository;

import com.fortickets.common.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

     Optional<Booking> findByScheduleIdAndSeat(Long scheduleId, String seat);

     Page<Booking> findByUserId(Long userId, Pageable pageable);

    List<Booking> findByPaymentId(Long paymentId);

     List<Booking> findAllByIdInAndStatus(List<Long> bookingIds, BookingStatus status);
}
