package com.fortickets.orderservice.domain.repository;

import com.fortickets.common.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingRepositoryCustom {

    Page<Booking> findByBookingSearch(List<Long> userIds, List<Long> concertIds, Pageable pageable);

    List<String> findSeatByScheduleId(Long scheduleId, BookingStatus pending, BookingStatus confirmed);

}
