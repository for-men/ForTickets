package com.fortickets.userservice.application.dto.requset;

public record UpdateUserReq (
     String nickname,
     String password,
     String phone,
     String profileImage)
{}
