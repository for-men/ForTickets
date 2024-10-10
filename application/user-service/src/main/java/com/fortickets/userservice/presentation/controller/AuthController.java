package com.fortickets.userservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.common.CommonResponse.CommonEmptyRes;
import com.fortickets.userservice.application.dto.requset.LoginReq;
import com.fortickets.userservice.application.dto.requset.SignUpReq;
import com.fortickets.userservice.application.security.JwtUtil;
import com.fortickets.userservice.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/sign-up")
    public CommonResponse<CommonEmptyRes> signUp(@Valid @RequestBody SignUpReq req) {
        authService.signUp(req);
        return CommonResponse.success();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<CommonEmptyRes>> login(@RequestBody LoginReq req) {
        String token = authService.login(req);

        return ResponseEntity.ok()
            .header(JwtUtil.AUTHORIZATION_HEADER, token) // Authorization 헤더에 토큰 추가
            .body(CommonResponse.success()); // 성공 메시지 반환
    }
}
