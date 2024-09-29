package com.fortickets.userservice.presentation.controller;

import com.fortickets.userservice.application.dto.response.UserInfoRes;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.application.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoRes> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUser().getEmail();

        UserInfoRes userInfo = userService.getUserInfo(email);
        return ResponseEntity.ok(userInfo);
    }
}
