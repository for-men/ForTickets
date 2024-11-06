package com.fortickets.orderservice.application.dto.response;

public record CreatePaymentRes(
    Long paymentId,
    Long userId,
    Long concertId,
    Long totalPrice
) {

}
