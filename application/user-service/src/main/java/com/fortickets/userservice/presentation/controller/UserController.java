package com.fortickets.userservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.common.CommonResponse.CommonEmptyRes;
import com.fortickets.userservice.application.dto.requset.UpdateUserReq;
import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.application.security.CustomAuthentication;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.application.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public CommonResponse<CommonEmptyRes> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid  @RequestBody UpdateUserReq req) {
        Long userId = userDetails.getUser().getUserId();
        userService.updateUserInfo(userId, req);
        return CommonResponse.success();
    }

    // Common용 JwtAutenticationFilter 테스트를 위한 코드입니다
    @PreAuthorize("hasRole('MANAGER')") // 역할에 ROLE_을 추가
    @GetMapping("/test")
    public CommonResponse<Object> getAuthenticatedUserInfo() {
        // 현재 인증된 사용자의 Authentication 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // CustomAuthentication으로 형변환
        CustomAuthentication customAuth = (CustomAuthentication) authentication;

        // 사용자 정보 (userId, email 등)
        Long userId = customAuth.getUserId();
        String email = customAuth.getEmail();

        // 권한 정보 (role 등)
        Object authority = authentication.getAuthorities();

        // 사용자 정보와 권한 정보를 함께 리턴
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("userId", userId);
        response.put("roles", authority);

        return CommonResponse.success(response);
    }

    // feignClient test
//    @GetMapping("/test/hello")
//    public String testHello() {
//        return userService.callOrderHello(); // UserService의 메서드를 통해 호출
//    }
}
