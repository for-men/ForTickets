package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.BookingStatus;

public record GetBookingRes (
    Long id,
    Long scheduleId,
    Long userId,
    Long price,
    BookingStatus status,
    String seat
){

}
