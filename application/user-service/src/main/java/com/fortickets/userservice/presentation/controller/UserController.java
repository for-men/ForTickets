package com.fortickets.userservice.presentation.controller;

import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.application.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    // 현재 로그인한 사용자의 정보 조회
    @GetMapping("/users")
    public ResponseEntity<GetUserRes> getCurrentUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();

        GetUserRes userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    // 전체 사용자의 정보 조회
//    @PreAuthorize("hasRole(ROLE_MANAGER)")
//    @GetMapping("/users/all")
//    public ResponseEntity<List<GetUserRes>> GetAllUsersInfo() {
//        List<GetUserRes> users = userService.getUserInfo();
//        return ResponseEntity.ok();
//    }

    // 사용자 정보 수정

    // 회원 탈퇴
}
