package com.fortickets.concertservice.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record UpdateConcertReq(

    String concertName,

    @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|png)$|^$",
        message = "이미지는 유효한 URL이어야 하며, jpg, png 형식의 파일이어야 합니다.")
    String concertImage,

    @Positive(message = "가격은 양수여야 합니다.")
    Integer runtime,

    LocalDate startDate,

    LocalDate endDate,

    @Positive(message = "가격은 양수여야 합니다.")
    Long price
) {

}
