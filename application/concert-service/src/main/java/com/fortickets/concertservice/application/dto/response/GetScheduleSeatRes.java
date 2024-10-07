package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record GetScheduleSeatRes(
    Long id,
    Long concertId,
    GetStageSeatRes stage,
    List<String> seatList,
    LocalDate concertDate,
    LocalTime concertTime) {

}
