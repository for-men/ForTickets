package com.fortickets.concertservice.application.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateScheduleReq(

    Long stageId,

    LocalDate concertDate,

    LocalTime concertTime
) {

}
