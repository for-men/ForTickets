package com.fortickets.userservice.application.service;

import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    // 현재 로그인한 사용자 정보 조회
    public GetUserRes getUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new GetUserRes(user.getUserId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getProfileImage());
    }
}
