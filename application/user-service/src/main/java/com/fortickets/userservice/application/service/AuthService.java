package com.fortickets.userservice.application.service;

import com.fortickets.userservice.application.dto.LoginReq;
import com.fortickets.userservice.application.dto.SignUpReq;
import com.fortickets.userservice.application.security.JwtUtil;
import com.fortickets.userservice.application.security.UserDetailsImpl;
import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.entity.UserRoleEnum;
import com.fortickets.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final String SELLER_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private final String MANAGER_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원가입
    @Transactional
    public void signUp(SignUpReq req) {
        String nickname = req.getNickname();
        String email = req.getEmail();
        String password = passwordEncoder.encode(req.getPassword());
        String phone = req.getPhone();
        String profileImage = req.getProfileImage();

        // 회원 중복 확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 ROLE 확인
        boolean isSeller = false;
        boolean isManager = false;

        // 기본고객
        UserRoleEnum role = UserRoleEnum.USER;

        // 판매자
        if (req.isSeller()) {
            if (!SELLER_TOKEN.equals(req.getSellerToken())) {
                throw new IllegalArgumentException("코드가 틀려 등록이 불가능합니다.");
            }
            isSeller = true;
            role = UserRoleEnum.SELLER;
        }

        // 관리자
        if (req.isManager()) {
            if (!MANAGER_TOKEN.equals(req.getManagerToken())) {
                throw new IllegalArgumentException("코드가 틀려 등록이 불가능합니다.");
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

        if (req.getEmail() == null || req.getEmail().isEmpty() || req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new RuntimeException("이메일 또는 비밀번호가 비어있습니다.");
        }

        try {
            // 사용자 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );

            // 인증 성공 시 사용자 정보 가져오기
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            Long userId = userDetails.getUser().getUserId();
            String username = userDetails.getUsername(); // 실제로는 email
            UserRoleEnum role = userDetails.getUser().getRole();

            // JWT 토큰 생성
            return jwtUtil.createToken(userId, username, role);
        } catch (AuthenticationException e) {
            throw new RuntimeException("잘못된 이메일 혹은 비밀번호를 입력했습니다.");
        }
    }
}
