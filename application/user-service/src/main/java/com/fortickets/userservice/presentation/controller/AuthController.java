package com.fortickets.userservice.presentation.controller;

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
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpReq req) {
        authService.signUp(req);
        return ResponseEntity.ok("회원가입을 성공하였습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginReq req) {
        String token = authService.login(req);
        // 토큰을 포함하여 응답 반환
        return ResponseEntity.ok()
            .header(JwtUtil.AUTHORIZATION_HEADER, token) // Authorization 헤더에 토큰 추가
            .body("로그인을 성공하였습니다.");
    }
}
