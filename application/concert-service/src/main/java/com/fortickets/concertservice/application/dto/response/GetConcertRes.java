package com.fortickets.concertservice.application.dto.response;

import java.util.Date;

public record GetConcertRes(
    Long concertId,
    Long userId,
    String concertName,
    String concertImage,
    int runtime,
    Date startDate,
    Date endDate,
    Long price
) {

}
