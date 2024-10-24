package com.fortickets.concertservice.application.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateStageReq(

    @Size(min = 1, max = 100, message = "공연장 이름은 1자 이상 100자 이하이어야 합니다.")
    String name,

    @Size(min = 1, max = 255, message = "공연장 장소가 너무 깁니다.")
    String location,

    @Positive(message = "가격은 양수여야 합니다.")
    Integer row,

    @Positive(message = "가격은 양수여야 합니다.")
    Integer col
) {

}
