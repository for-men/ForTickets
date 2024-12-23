package com.fortickets.orderservice.application.dto.request;

public record DecrementSeatsRollbackReq(
    Integer quantity,
    Long scheduleId
) {

}
