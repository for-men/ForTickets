package com.fortickets.userservice.application.dto;

import com.fortickets.userservice.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpReq {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9]{4,10}$", //최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성
            message = "아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]*$", //최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{10,11}$", // 10자리 또는 11자리 숫자
            message = "전화번호는 10자리 또는 11자리 숫자여야 합니다.")
    private String phone;

    @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|png)$", // 이미지 파일 확장자가 jpg, gif, png인 URL 형식
            message = "프로필 이미지는 유효한 URL이어야 하며, jpg, png 형식의 파일이어야 합니다.")
    private String profileImage;

    // 새로운 필드 추가
//    @NotNull(message = "역할은 필수 입력 사항입니다.")
//    private UserRoleEnum role;

    private boolean isDelete = false;

    private boolean isSeller = false;

    private boolean isManager = false;

    private String SellerToken = "";

    private String ManagerToken = "";
}
