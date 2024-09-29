package com.fortickets.userservice.application.service;

import com.fortickets.userservice.application.dto.response.UserInfoRes;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public UserInfoRes getUserInfo(String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserInfoRes(user.getUserId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getProfileImage());
    }

}
