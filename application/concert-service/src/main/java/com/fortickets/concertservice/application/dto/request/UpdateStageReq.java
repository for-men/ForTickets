package com.fortickets.concertservice.application.dto.request;

import jakarta.validation.constraints.Positive;

public record UpdateStageReq(

    String name,

    String location,

    @Positive(message = "가격은 양수여야 합니다.")
    Integer row,

    @Positive(message = "가격은 양수여야 합니다.")
    Integer col
) {

}
