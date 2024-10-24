package com.fortickets.userservice.application.dto.response;

public record GetUserRes(Long userId, String nickname, String email, String phone, String profileImage) {

}
