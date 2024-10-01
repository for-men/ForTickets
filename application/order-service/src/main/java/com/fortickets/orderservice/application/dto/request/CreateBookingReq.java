package com.fortickets.orderservice.application.dto.request;


import com.fortickets.common.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import java.util.List;

public record CreateBookingReq (
    Long scheduleId,
    Long concertId,
    Long userId,
    Long price,
    List<String> seat
){
    public Booking toEntity(String seat) {
        return new Booking(scheduleId(), concertId(), userId(), price(), seat);
    }
}
