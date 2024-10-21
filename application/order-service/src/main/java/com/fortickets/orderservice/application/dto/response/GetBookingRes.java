package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.util.BookingStatus;

public record GetBookingRes(
    Long id,
    Long concertId,
    Long scheduleId,
    Long paymentId,
    Long userId,
    Long price,
    BookingStatus status,
    String seat,
    String concertName,
    Integer runtime
) {

}
