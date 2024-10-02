package com.fortickets.orderservice.application.dto.response;

import java.util.Date;

public record GetScheduleRes (
    Long id,
    Long concertId,
    String concertName,
    Integer runtime,
    String stageName,
    String location,
    Date concertDate,
    Date concertTime
){

}
