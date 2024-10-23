package com.fortickets.concertservice.application.dto.request;

import com.fortickets.concertservice.domain.entity.Concert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public record CreateConcertReq (
    @NotBlank(message = "콘서트 이름은 비어 있을 수 없습니다.")
    @Size(min = 1, max = 20, message = "공연장 이름은 1자 이상 20자 이하이어야 합니다.")
    String concertName,

    @Positive(message = "런타임은 양수여야 합니다.")
    @NotNull(message = "런타임은 비어 있을 수 없습니다.")
    int runTime,

    @NotNull(message = "시작 날짜는 비어 있을 수 없습니다.")
    LocalDate startDate,

    @NotNull(message = "종료 날짜는 비어 있을 수 없습니다.")
    LocalDate endDate,

    @Positive(message = "가격은 양수여야 합니다.")
    @NotNull(message = "가격은 비어 있을 수 없습니다.")
    Long price,

    @NotBlank(message = "콘서트 이미지는 비어 있을 수 없습니다.")
    @NotBlank(message = "포스터 이미지는 비어 있을 수 없습니다.")
    @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|png)$|^$",
        message = "이미지는 유효한 URL이어야 하며, jpg, png 형식의 파일이어야 합니다.")
    String concertImage
){

  public Concert toEntity(Long userId) {
    return Concert.of(userId,concertName,runTime,startDate,endDate,price,concertImage);
  }

}
