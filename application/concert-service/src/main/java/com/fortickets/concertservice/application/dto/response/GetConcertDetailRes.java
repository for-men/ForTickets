package com.fortickets.concertservice.application.dto.response;

public record GetConcertDetailRes(
    Long id,
    String concertName,
    int runtime,
    Long price
) {

}
