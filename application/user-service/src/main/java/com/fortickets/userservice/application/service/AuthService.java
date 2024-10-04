package com.fortickets.userservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.fortickets.userservice.application.dto.requset.LoginReq;
import com.fortickets.userservice.application.dto.requset.SignUpReq;
import com.fortickets.userservice.application.security.JwtUtil;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.entity.UserRoleEnum;
import com.fortickets.userservice.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private  String SELLER_CODE;

    @Value("${service.access.manager-code}")
    private  String MANAGER_CODE;

    // 회원가입
    @Transactional
    public void signUp(SignUpReq req) {
        String nickname = req.nickname();
        String email = req.email();
        String password = passwordEncoder.encode(req.password());
        String phone = req.phone();
        String profileImage = req.profileImage();

        // 회원 중복 확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_EMAIL); // 중복된 사용자 예외 처리
        }

        // 닉네임 중복 확인
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_NICKNAME); // 중복된 닉네임 예외 처리
        }

        // 전화번호 중복 확인
        Optional<User> checkPhone = userRepository.findByPhone(phone);
        if (checkPhone.isPresent()) {
            throw new GlobalException(ErrorCase.DUPLICATE_PHONE); // 중복된 전화번호 예외 처리
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
            // 사용자 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    req.email(),
                    req.password()
                )
            );

            // 인증 성공 시 사용자 정보 가져오기
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            Long userId = userDetails.getUser().getUserId();
            String username = userDetails.getUsername(); // 메서드는 getUsername 이지만 return값은 email 입니다.
            UserRoleEnum role = userDetails.getUser().getRole();

            // JWT 토큰 생성 - 로그를위해 임시
            String jwtToken = jwtUtil.createAccessToken(userId, username, role);

            log.info("Issued JWT Token: {}", jwtToken); // 발급된 JWT 로그 추가
            // JWT 토큰 생성
//            return jwtUtil.createAccessToken(userId, username, role);
            return jwtToken;
        } catch (AuthenticationException e) {
            throw new GlobalException(ErrorCase.INVALID_EMAIL_OR_PASSWORD);
        }
    }
}
