package com.fortickets.orderservice.application.dto.response;

public record GetConcertRes(
    Long concertId,
    String concertName,
    Integer runtime
) {

}
