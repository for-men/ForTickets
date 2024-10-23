package com.fortickets.userservice.presentation.controller;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
import com.fortickets.userservice.application.dto.requset.UpdateUserReq;
import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.application.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public CommonResponse<GetUserRes> getCurrentUserInfo(@UseAuth CustomUser customUser) {
        Long userId = customUser.getUserId();
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
    public CommonResponse<CommonEmptyRes> updateUserInfo(@UseAuth CustomUser customAuth,
        @Valid @RequestBody UpdateUserReq req) {
        Long userId = customAuth.getUserId();
        userService.updateUserInfo(userId, req);
        return CommonResponse.success();
    }

    @GetMapping("/{userId}")
    public GetUserRes getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/{nickname}/nickname")
    public List<GetUserRes> searchNickname(@PathVariable String nickname) {
        return userService.searchNickname(nickname);
    }

}
