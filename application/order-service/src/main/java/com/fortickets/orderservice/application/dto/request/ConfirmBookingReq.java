package com.fortickets.orderservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ConfirmBookingReq(
    @NotNull Long userId,
    @NotNull List<Long> bookingIds
) {

}
