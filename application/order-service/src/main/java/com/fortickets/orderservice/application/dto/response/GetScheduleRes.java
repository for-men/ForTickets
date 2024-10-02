package com.fortickets.orderservice.application.dto.response;

public record GetScheduleRes (
    Long id,
    Long concertId,
    String concertName,
    Integer runtime
){

}
