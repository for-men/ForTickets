package com.fortickets.orderservice.application.dto.request;

import java.util.List;

public record CreatePaymentReq (
    Long userId,
    Long concertId,
    Long scheduleId,
    Long totalPrice,
    String card,
    List<Long> bookingIds
){

}
