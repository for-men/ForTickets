package com.fortickets.userservice.application.dto.requset;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpReq(
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]{4,10}$",
        message = "아이디는 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다.")
    String nickname,

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]*$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    String password,

    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{10,11}$",
        message = "전화번호는 10자리 또는 11자리 숫자여야 합니다.")
    String phone,

    @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|png)$",
        message = "프로필 이미지는 유효한 URL이어야 하며, jpg, png 형식의 파일이어야 합니다.")
    String profileImage,

    @JsonProperty("isDelete") boolean isDelete,
    @JsonProperty("isSeller") boolean isSeller,
    @JsonProperty("isManager") boolean isManager,

    @JsonProperty("sellerToken") String sellerToken,
    @JsonProperty("managerToken") String managerToken
) {

}