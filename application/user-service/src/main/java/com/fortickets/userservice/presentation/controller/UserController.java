package com.fortickets.userservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.userservice.application.dto.requset.UpdateUserReq;
import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.application.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    // 현재 로그인한 사용자의 정보 조회
    @GetMapping
    public CommonResponse<GetUserRes> getCurrentUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        GetUserRes userInfo = userService.getUserInfo(userId);
        return CommonResponse.success(userInfo);
    }

    // 전체 사용자의 정보 조회
    @PreAuthorize("hasRole('MANAGER')") // 역할에 ROLE_을 추가
    @GetMapping("/all")
    public CommonResponse<List<GetUserRes>> getAllUsersInfo() {
        List<GetUserRes> users = userService.getAllUsersInfo();
        return CommonResponse.success(users);
    }

    // 사용자 정보 수정
    @PutMapping
    public CommonResponse<String> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid  @RequestBody UpdateUserReq req) {
        Long userId = userDetails.getUser().getUserId();
        userService.updateUserInfo(userId, req);
        return CommonResponse.success("회원정보 수정을 성공하였습니다.");
    }
}
