package com.fortickets.orderservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record RequestPaymentReq(
    @NotNull Long paymentId,
    @NotNull String card
) {

}
