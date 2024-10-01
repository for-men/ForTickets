package com.fortickets.userservice.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${service.jwt.secret-key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public JwtAuthorizationFilter(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 요청의 헤더에서 JWT 토큰을 추출
        String tokenValue = getJwtFromHeader(req);
        log.info("Extracted JWT Token from Header: {}", tokenValue); // 토큰 값 로그로 확인

        // 토큰이 존재하고, 유효한지 확인, 유효하지 않으면 로그를 남기고 필터 체인을 중단
        if (StringUtils.hasText(tokenValue)) {
            try {
                // JWT 검증 필터에서 검증된 토큰만 이 필터로 들어올 것입니다.
                // 따라서, 단순히 유효성 검증을 하지 않습니다.

                // JWT 토큰에서 Claims 추출
                Claims info = extractClaimsFromToken(tokenValue);
                log.info("Extracted Claims: {}", info); // 추출된 사용자 정보 로그

                // 사용자 정보를 기반으로 인증을 설정
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
                return;
            }
        }

        // 인증이 완료된 후, 다음 필터로 요청을 전달
        filterChain.doFilter(req, res);
    }

    // JWT 토큰을 요청 헤더에서 가져오는 메서드
    private String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 실제 토큰 값 추출
        }
        return null;
    }

    // JWT 토큰에서 Claims 추출
    private Claims extractClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // 인증 처리
    // 사용자 아이디를 사용하여 인증 객체를 생성하고, 이를 Spring Security의 SecurityContext에 설정
    public void setAuthentication(String username) {
        if (username == null || username.isEmpty()) {
            log.error("Username is null or empty");
            return;
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            log.error("UserDetails is null for username: {}", username);
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
