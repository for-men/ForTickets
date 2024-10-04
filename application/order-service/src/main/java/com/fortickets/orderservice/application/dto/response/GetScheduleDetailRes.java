package com.fortickets.orderservice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetScheduleDetailRes(
    Long id,
    Long concertId,
    String concertName,
    Integer runtime,
    String stageName,
    String location,
    LocalDate concertDate,
    LocalTime concertTime
){

}
