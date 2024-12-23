package com.fortickets.orderservice.application.dto.response;

import java.util.List;

public record CreateBookingAndPaymentRes(
    List<CreateBookingRes> bookings,
    CreatePaymentRes payment
) {

}
