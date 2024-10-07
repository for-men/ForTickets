package com.fortickets.concertservice.application.dto.request;

public record UpdateStageReq(
    String name,
    String location,
    Integer row,
    Integer col
) {

}
