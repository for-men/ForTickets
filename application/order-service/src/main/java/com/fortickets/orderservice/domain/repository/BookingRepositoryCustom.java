package com.fortickets.orderservice.domain.repository;


import com.fortickets.common.util.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {

    Page<Booking> findByBookingSearch(List<Long> userIds, List<Long> concertIds, Pageable pageable);

    List<String> findSeatByScheduleId(Long scheduleId, BookingStatus pending, BookingStatus confirmed);

}
