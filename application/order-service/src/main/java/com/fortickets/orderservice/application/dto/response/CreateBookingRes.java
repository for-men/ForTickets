package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.BookingStatus;

public record CreateBookingRes (
    Long id,
    Long scheduleId,
    Long userId,
    Long price,
    BookingStatus status,
    String seat
) {

}
