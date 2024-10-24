package com.fortickets.userservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.userservice.application.dto.requset.LoginReq;
import com.fortickets.userservice.application.dto.requset.SignUpReq;
import com.fortickets.userservice.application.security.JwtUtil;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.entity.UserRoleEnum;
import com.fortickets.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${service.access.seller-code}")
    private String SELLER_CODE;

    @Value("${service.access.manager-code}")
    private String MANAGER_CODE;

    // 회원가입
    @Transactional
    public void signUp(SignUpReq req) {
        String nickname = req.nickname();
        String email = req.email();
        String password = passwordEncoder.encode(req.password());
        String phone = req.phone();
        String profileImage = req.profileImage();

        // 회원 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_EMAIL);
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_NICKNAME);
        }
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_PHONE);
        }

        // 사용자 ROLE 확인
        boolean isSeller = false;
        boolean isManager = false;

        // 기본고객
        UserRoleEnum role = UserRoleEnum.USER;

        // 판매자
        if (req.isSeller()) {
            if (!SELLER_CODE.equals(req.sellerToken())) {
                throw new GlobalException(ErrorCase.INVALID_SELLER_CODE); // 잘못된 판매자 코드 예외 처리
            }
            isSeller = true;
            role = UserRoleEnum.SELLER;
        }

        // 관리자
        if (req.isManager()) {
            if (!MANAGER_CODE.equals(req.managerToken())) {
                throw new GlobalException(ErrorCase.INVALID_MANAGER_CODE); // 잘못된 관리자 코드 예외 처리
            }
            isManager = true;
            role = UserRoleEnum.MANAGER;
        }

        // 사용자 등록
        User user = new User(nickname, email, password, phone, profileImage, role);
        userRepository.save(user);
    }

    // 로그인
    public String login(LoginReq req) {

        if (req.email() == null || req.email().isEmpty() || req.password() == null || req.password().isEmpty()) {
            throw new GlobalException(ErrorCase.EMPTY_EMAIL_OR_PASSWORD);
        }

        try {
            User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new GlobalException(ErrorCase.INVALID_EMAIL_OR_PASSWORD));

            if (!passwordEncoder.matches(req.password(), user.getPassword())) {
                throw new GlobalException(ErrorCase.INVALID_EMAIL_OR_PASSWORD);
            }

            // JWT 토큰 생성
            String jwtToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole());
            log.info("Issued JWT Token: {}", jwtToken); // 발급된 JWT 로그 추가

            return jwtToken;
        } catch (AuthenticationException e) {
            throw new GlobalException(ErrorCase.INVALID_EMAIL_OR_PASSWORD);
        }
    }
}
