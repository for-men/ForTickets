package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.util.PaymentStatus;

public record GetPaymentRes(
    Long totalPrice,
    PaymentStatus status,
    String card,
    String concertName,
    Long concertId,
    Long scheduleId,
    Long userId
) {

}
