package com.fortickets.concertservice.application.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateConcertReq(

    @Size(min = 1, max = 100, message = "공연장 이름은 1자 이상 100자 이하이어야 합니다.")
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
