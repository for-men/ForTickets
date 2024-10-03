package com.fortickets.concertservice.application.dto;

public record CreateStageRes(
    Long stageId,
    String name,
    String location,
    int row,
    int col
) {

}
