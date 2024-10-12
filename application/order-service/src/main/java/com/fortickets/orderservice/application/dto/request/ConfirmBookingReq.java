package com.fortickets.orderservice.application.dto.request;

import jakarta.validation.Valid;
import java.util.List;

public record ConfirmBookingReq(
    @Valid Long userId,
    @Valid List<Long> bookingIds
) {

}
