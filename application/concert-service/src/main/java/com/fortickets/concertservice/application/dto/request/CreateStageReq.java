package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateStageReq(
    @NotBlank(message = "공연장 이름은 비어 있을 수 없습니다.")
    String name,

    @NotBlank(message = "위치는 비어 있을 수 없습니다.")
    String location,

    @Positive(message = "행 수는 양수여야 합니다.")
    Integer row,

    @Positive(message = "열 수는 양수여야 합니다.")
    Integer col
) {

  public Stage toEntity() {
    return Stage.of(name,location,row,col);
  }

}
