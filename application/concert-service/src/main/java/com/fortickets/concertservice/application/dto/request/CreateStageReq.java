package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateStageReq(
    @NotBlank(message = "공연장 이름은 비어 있을 수 없습니다.")
    @Size(min = 1, max = 20, message = "공연장 이름은 1자 이상 30자 이하이어야 합니다.")
    String name,

    @NotBlank(message = "위치는 비어 있을 수 없습니다.")
    @Size(min = 1, max = 30, message = "위치는 1자 이상 50자 이하이어야 합니다.")
    String location,

    @Positive(message = "행 수는 양수여야 합니다.")
    @NotNull(message = "행 수는 비어 있을 수 없습니다.")
    Integer row,

    @Positive(message = "열 수는 양수여야 합니다.")
    @NotNull(message = "열 수는 비어 있을 수 없습니다.")
    Integer col
) {

  public Stage toEntity() {
    return Stage.of(name,location,row,col);
  }

}
