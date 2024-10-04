package com.fortickets.orderservice.application.dto.response;

public record GetUserRes(
    Long userId,
    String nickname,
    String email,
    String phone
) {

}
