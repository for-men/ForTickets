package com.fortickets.orderservice.application.dto.request;

import java.util.List;

public record ConfirmBookingReq (
    List<Long> bookingIds
) {

}
