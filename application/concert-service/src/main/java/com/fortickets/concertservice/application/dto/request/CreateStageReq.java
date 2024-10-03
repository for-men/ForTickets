package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Stage;

public record CreateStageReq(
    String name,
    String location,
    int row,
    int col
) {

  public Stage toEntity() {
    return Stage.of(name,location,row,col);
  }

}
