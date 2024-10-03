package com.fortickets.concertservice.application.dto.response;

public record GetStageRes(
    Long stageId,
    String name,
    String location,
    int row,
    int col
) {

}
