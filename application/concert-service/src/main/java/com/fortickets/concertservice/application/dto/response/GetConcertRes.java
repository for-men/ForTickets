package com.fortickets.concertservice.application.dto.response;

import com.fortickets.concertservice.domain.entity.Schedule;
import java.time.LocalDate;
import java.util.List;

public record GetConcertRes(
    Long id,
    String concertName,
    String concertImage,
    int runtime,
    Long price,
    LocalDate startDate,
    LocalDate endDate,
    List<GetScheduleRes> schedules
) {

}
