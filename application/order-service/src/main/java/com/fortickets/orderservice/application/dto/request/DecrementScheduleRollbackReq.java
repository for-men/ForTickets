package com.fortickets.orderservice.application.dto.request;

public record DecrementScheduleRollbackReq(
    Integer quantity,
    Long scheduleId
) {

}
