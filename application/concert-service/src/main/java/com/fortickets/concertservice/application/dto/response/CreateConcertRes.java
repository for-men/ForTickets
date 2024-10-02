package com.fortickets.concertservice.application.dto.response;

import java.sql.Date;

public record CreateConcertRes(
    Long concertId,
    Long userId,
    String concertName,
    int runtime,
    Date startDate,
    Date endDate,
    Long price,
    String concertImage
) {

}
