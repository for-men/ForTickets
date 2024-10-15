package com.fortickets.orderservice.application.dto;

public record CreatePaymentRes(
    Long paymentId,
    Long userId,
    Long concertId,
    Long scheduleId
) {

}
