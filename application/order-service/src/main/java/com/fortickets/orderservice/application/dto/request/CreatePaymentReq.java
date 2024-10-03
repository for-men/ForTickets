package com.fortickets.orderservice.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record CreatePaymentReq (
    Long userId,
    Long concertId,
    Long scheduleId,
    Long totalPrice,
    List<Long> bookingIds
){

}
