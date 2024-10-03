package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;

public record CreateConcertRes(
    Long id,
    Long userId,
    String concertName,
    int runtime,
    LocalDate startDate,
    LocalDate endDate,
    Long price,
    String concertImage
) {

}
