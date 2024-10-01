package com.fortickets.orderservice.application.dto.request;


import java.util.List;

public record CreateBookingReq (
    Long scheduleId,
    Long userId,
    Long price,
    List<String> seat
){

}
