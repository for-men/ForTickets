package com.fortickets.concertservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateScheduleReq(

    Long stageId,

    LocalDate concertDate,

    LocalTime concertTime
) {

}
