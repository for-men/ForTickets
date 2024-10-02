package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Concert;
import java.sql.Date;

public record CreateConcertReq (
    String concertName,
    int runTime,
    Date startDate,
    Date endDate,
    Long price,
    String concertImage
){

  public Concert toEntity(Long userId) {
    return Concert.of(userId,concertName,runTime,startDate,endDate,price,concertImage);
  }

}
