package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.Schedule;
import com.fortickets.concertservice.domain.entity.Stage;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateScheduleReq(
    @NotNull(message = "콘서트 ID는 비어 있을 수 없습니다.")
    Long concertId,

    @NotNull(message = "스테이지 ID는 비어 있을 수 없습니다.")
    Long stageId,

    @NotNull(message = "콘서트 날짜는 비어 있을 수 없습니다.")
    LocalDate concertDate,

    @NotNull(message = "콘서트 시간은 비어 있을 수 없습니다.")
    LocalTime concertTime
) {

    public Schedule toEntity(Concert concert, Stage stage) {
        return Schedule.of(concert, stage, concertDate, concertTime);
    }

}
