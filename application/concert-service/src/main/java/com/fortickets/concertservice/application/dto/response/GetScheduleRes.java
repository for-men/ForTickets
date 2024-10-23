package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetScheduleRes(
    Long id,
    Long stageId,
    LocalDate concertDate,
    LocalTime concertTime
) {

}
