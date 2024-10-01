package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.BookingStatus;

public record GetBookingRes (
    Long id,
    Long concertId,
    Long userId,
    Long price,
    BookingStatus status,
    String seat
){

}
