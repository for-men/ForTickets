package com.fortickets.orderservice.application.dto.response;

import java.time.LocalDateTime;

public record GetPaymentDetailRes(
    Long id,
    Long concertId,
    Long scheduleId,
    Long userId,
    Long totalPrice,
    String concertName,
    Integer runtime,
    String seat,
    String nickname,
    // TODO: 결제시간 추가 필요
    LocalDateTime createdAt
) {

}
