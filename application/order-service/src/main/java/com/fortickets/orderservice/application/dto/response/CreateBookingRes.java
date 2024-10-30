package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.util.BookingStatus;

public record CreateBookingRes(
    Long id,
    Long concertId,
    Long scheduleId,
    Long userId,
    Long price,
    BookingStatus status,
    String seat
) {

}
