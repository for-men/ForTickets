package com.fortickets.concertservice.application.dto.response;

public record CreateStageRes(
    Long id,
    String name,
    String location,
    int row,
    int col
) {

}