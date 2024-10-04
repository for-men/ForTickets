package com.fortickets.userservice.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT에서 정보 추출")
public class JwtAuthenticationFilterForUserService extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilterForUserService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        // 요청 URI 가져오기
        String requestURI = req.getRequestURI();

        // /auth/** 경로에 대해서는 필터를 타지 않고 바로 통과시킴
        if (requestURI.startsWith("/auth")) {
            filterChain.doFilter(req, res);
            return;
        }

        // 헤더에서 사용자 정보 추출
        String userId = req.getHeader("X-User-Id");
        String email = req.getHeader("X-Email");
        String role = req.getHeader("X-Role");

        // 로그 출력
        log.info("Extracted Headers: userId={}, email={}, role={}", userId, email, role);

        // 사용자 이름이나 역할이 없으면 401 상태 코드 반환
        if (userId == null || userId.isEmpty() || email == null || email.isEmpty() || role == null || role.isEmpty()) {
            log.error("userId or role is null or empty");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            return;
        }

        // 인증 설정
        setAuthentication(email, role); // 역할을 포함하여 인증 설정

        // 다음 필터로 요청을 전달
        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email, String role) {
        if (email == null || email.isEmpty()) {
            log.error("email is null or empty");
            return;
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email, role); // 역할 포함
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email, String role) { // 역할 추가
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails == null) {
            log.error("UserDetails is null for email: {}", email);
            return null;
        }

        // 역할 추가
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(role)); // 역할을 권한에 추가

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
