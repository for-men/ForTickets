package com.fortickets.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "Common용")
@Component
@RequiredArgsConstructor
public class CustomPreFilter extends OncePerRequestFilter {

//    private static final List<RequestMatcher> whiteList =
//        List.of(new AntPathRequestMatcher("/auth", HttpMethod.POST.name()),
//            new AntPathRequestMatcher("/toss", HttpMethod.GET.name())
//        );

    // 필터가 실행되지 않도록 할 경로 지정
    @Override
    protected void doFilterInternal(HttpServletRequest req,
        HttpServletResponse res,
        FilterChain filterChain) throws ServletException, IOException {

        // 요청 URI 가져오기
        String requestURI = req.getRequestURI();

        // 토큰이 없이 통과시켜야 할 경로에 대해서는 필터를 타지 않고 바로 통과시킴 ex) /auth/**
        if (requestURI.startsWith("/auth")
            || requestURI.startsWith("/actuator")
            || requestURI.startsWith("/widget")
            || requestURI.startsWith("/orders/html")) {
            filterChain.doFilter(req, res);
            return;
        }

        // 헤더에서 사용자 정보 추출
        String userIdHeader = req.getHeader("X-User-Id");
        String email = req.getHeader("X-Email");
        String role = req.getHeader("X-Role");

        // 로그 출력
        log.info("Extracted Headers: userId={}, email={}, role={}", userIdHeader, email, role);

        // 사용자 이름이나 역할이 없으면 401 상태 코드 반환
        if (userIdHeader == null || userIdHeader.isEmpty() || email == null || email.isEmpty() || role == null || role.isEmpty()) {
            log.error("userId or email or role is null or empty");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            return;
        }

        // Long 타입으로 변환
        Long userId;
        try {
            userId = Long.valueOf(userIdHeader); // Long 타입으로 변환
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userIdHeader);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            return;
        }

        // 사용자 정보로 Authentication 객체 생성
        Authentication authentication = createAuthentication(userId, email, role);

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // SecurityContext에 저장된 인증 객체 확인
        Authentication storedAuth = SecurityContextHolder.getContext().getAuthentication();
        log.info("SecurityContext에 저장된 인증 객체: {}", storedAuth.getPrincipal());
        // 다음 필터로 요청 전달
        filterChain.doFilter(req, res);
    }

    // Authentication 객체 생성 (사용자 정보와 권한 설정)
    private Authentication createAuthentication(Long userId, String email, String role) {
        // 권한 설정
        var authorities = AuthorityUtils.createAuthorityList(role);
        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }

//    /**
//     * shouldNotFilter 는 true 를 반환하면 필터링 통과시키는 메서드.
//     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        // 현재 URL 이 화이트 리스트에 존재하는지 체크
//        return whiteList.stream().anyMatch(whitePath -> whitePath.matches(request));
//    }

}
