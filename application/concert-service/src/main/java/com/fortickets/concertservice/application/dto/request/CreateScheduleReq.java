package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.Schedule;
import com.fortickets.concertservice.domain.entity.Stage;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateScheduleReq(
    Long concertId,
    Long stageId,
    LocalDate concertDate,
    LocalTime concertTime
) {

  public Schedule toEntity(Concert concert, Stage stage) {
    return Schedule.of(concert,stage,concertDate,concertTime);
  }

}
