package com.fortickets.concertservice.application.dto.request;

import java.time.LocalDate;

public record UpdateConcertReq(
    String concertName,
    String concertImage,
    Integer runtime,
    LocalDate startDate,
    LocalDate endDate,
    Long price
) {

}
