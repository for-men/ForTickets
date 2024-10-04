package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;

public record GetConcertRes(
    Long id,
    Long userId,
    String concertName,
    String concertImage,
    int runtime,
    LocalDate startDate,
    LocalDate endDate,
    Long price
) {

}
