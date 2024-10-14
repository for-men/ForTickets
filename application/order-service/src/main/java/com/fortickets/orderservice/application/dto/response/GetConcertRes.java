package com.fortickets.orderservice.application.dto.response;

public record GetConcertRes(
    Long id,
    String concertName,
    int runtime,
    Long price
) {

}
