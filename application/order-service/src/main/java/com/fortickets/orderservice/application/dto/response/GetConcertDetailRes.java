package com.fortickets.orderservice.application.dto.response;

import com.fortickets.common.util.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record GetConcertDetailRes(
    Long userId,
    Long price,
    BookingStatus status,
    String seat,
    Long scheduleId,
    Long concertId,
    String concertName,
    Integer runtime,
    String stageName,
    String location,
    LocalDate concertDate,
    LocalTime concertTime
) {

}
