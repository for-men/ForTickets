package com.fortickets.orderservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePaymentReq(
    @NotNull Long userId,
    @NotNull List<Long> bookingIds
) {

}
