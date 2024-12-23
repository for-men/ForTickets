package com.fortickets.orderservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record RequestPaymentReq(
    @NotNull String paymentId,
    @NotNull String orderId, // 추가된 필드
    @NotNull Long amount // 추가된 필드
) {

}
