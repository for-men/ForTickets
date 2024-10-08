package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record GetScheduleRes(
    Long id,
    Long stageId,
    LocalDate concertDate,
    LocalTime concertTime
) {

}
