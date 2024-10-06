package com.fortickets.userservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import com.fortickets.userservice.application.dto.requset.UpdateUserReq;
import com.fortickets.userservice.application.dto.response.GetUserRes;
import com.fortickets.userservice.application.mapper.UserMapper;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

//    private final OrderClient orderClient;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 추가
    private final UserMapper userMapper;

    // 현재 로그인한 사용자 정보 조회
    public GetUserRes getUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new GlobalException(ErrorCase.USER_NOT_FOUND));
        return userMapper.userToGetUserRes(user);
    }

    // 전체 사용자의 정보 조회
    public List<GetUserRes> getAllUsersInfo() {
        List<User> users = userRepository.findAll(); // 모든 사용자 조회
        return users.stream()
            .map(userMapper::userToGetUserRes)
            .collect(Collectors.toList());
    }

    // 사용자 정보 수정
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserReq req) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCase.USER_NOT_FOUND));
        // 닉네임 중복 확인 (현재 사용자 제외)
        if (userRepository.existsByNicknameAndUserIdNot(req.nickname(), userId)) {
            throw new GlobalException(ErrorCase.DUPLICATE_NICKNAME);
        }
        // 전화번호 중복 확인 (현재 사용자 제외)
        if (userRepository.existsByPhoneAndUserIdNot(req.phone(), userId)) {
            throw new GlobalException(ErrorCase.DUPLICATE_PHONE);
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(req.password());
        // 사용자 정보 업데이트
        userMapper.updateUserReqToUser(user, req, encodedPassword);
        // 업데이트 된 정보를 저장
        userRepository.save(user);
    }

    public GetUserRes getUser(Long userId) {
        var user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new GlobalException(ErrorCase.USER_NOT_FOUND));
        return userMapper.userToGetUserRes(user);
    }

    public List<GetUserRes> searchNickname(String nickname) {
        var users = userRepository.findByNicknameContaining(nickname);
        return users.stream().map(userMapper::userToGetUserRes).toList();
    }

    // feignClient test
//    public String callOrderHello() {
//        return orderClient.hello(); // OrderClient의 hello 메서드 호출
//    }
}
