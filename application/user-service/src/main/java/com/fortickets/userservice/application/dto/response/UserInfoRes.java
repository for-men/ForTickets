package com.fortickets.userservice.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRes {

    private Long userId;
    private String nickname;
    private String email;
    private String phone;
    private String profileImage;

    // 생성자
    public UserInfoRes(Long userId, String nickname, String email, String phone, String profileImage) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.profileImage = profileImage;
    }
}
