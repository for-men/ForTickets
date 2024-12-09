package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateScheduleRes(
    Long id,
    Long concertId,
    Long stageId,
    LocalDate concertDate,
    LocalTime concertTime,
    int remainingSeats) {


}
