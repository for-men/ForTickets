package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Concert;
import java.time.LocalDate;

public record CreateConcertReq (
    String concertName,
    int runTime,
    LocalDate startDate,
    LocalDate endDate,
    Long price,
    String concertImage
){

  public Concert toEntity(Long userId) {
    return Concert.of(userId,concertName,runTime,startDate,endDate,price,concertImage);
  }

}
