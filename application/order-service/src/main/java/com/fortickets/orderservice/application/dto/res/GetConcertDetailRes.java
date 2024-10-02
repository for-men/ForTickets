package com.fortickets.orderservice.application.dto.res;

import com.fortickets.common.BookingStatus;
import java.util.Date;

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
    Date concertDate,
    Date concertTime
) {

}
