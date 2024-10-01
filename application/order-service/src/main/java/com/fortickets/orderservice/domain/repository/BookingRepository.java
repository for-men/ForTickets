package com.fortickets.orderservice.domain.repository;

import com.fortickets.orderservice.domain.entity.Booking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

     Optional<Booking> findByScheduleIdAndSeat(Long scheduleId, String seat);
}
