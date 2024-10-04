package com.fortickets.orderservice.domain.repository;

import com.fortickets.common.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

     Optional<Booking> findByScheduleIdAndSeat(Long scheduleId, String seat);

     // 예매 대기 인 경우는 제외하고 예매 내역 조회
     @Query("SELECT b FROM Booking b WHERE "
         + "(:userIds IS NULL OR b.userId IN :userIds) "
         + "AND (:concertIds IS NULL OR b.concertId IN :concertIds) "
         + "AND b.status != :status")
     Page<Booking> findByBookingSearch(List<Long> userIds, List<Long> concertIds, BookingStatus status, Pageable pageable);

     Page<Booking> findByUserId(Long userId, Pageable pageable);

     @Query("SELECT b.seat FROM Booking b "
         + "WHERE b.scheduleId = :scheduleId "
         + "AND (b.status = :pending OR b.status = :confirmed)")
     List<String> findSeatByScheduleId(
         Long scheduleId,
          BookingStatus pending,
          BookingStatus confirmed);

}
