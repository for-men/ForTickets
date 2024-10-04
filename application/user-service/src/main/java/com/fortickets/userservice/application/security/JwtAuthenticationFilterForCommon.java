//package com.fortickets.userservice.application.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import javax.crypto.SecretKey;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Slf4j(topic = "Common용")
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilterForCommon extends OncePerRequestFilter {
//
//    @Value("${service.jwt.secret-key}") // Base64 Encode 한 SecretKey
//    private String secretKey;
//    private SecretKey key;
//
//    @PostConstruct
//    public void init() {
//        byte[] bytes = Base64.getDecoder().decode(secretKey);
//        key = Keys.hmacShaKeyFor(bytes);
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//        HttpServletResponse response,
//        FilterChain filterChain) throws ServletException, IOException {
//
//        // 필터가 처음 시작되면 SecretKey 초기화
//        if (key == null) {
//            byte[] decodedKey = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
//            key = Keys.hmacShaKeyFor(decodedKey);
//        }
//
//        // 헤더에서 토큰 추출
//        String token = getJwtFromHeader(request);
//
//        if (token != null) {
//            // 토큰에서 사용자 정보 추출
//            Claims claims = getClaimsFromToken(token);
//            Long userId = claims.get("userId", Long.class);
//            String email = claims.get("email", String.class);
//            String role = claims.get("role", String.class);
//
//            // 추출 확인 로그
//            log.info("Extracted from token: userId={}, email={}, role={}", userId, email, role);
//
//            // 사용자 정보로 Authentication 객체 생성
//            Authentication authentication = createAuthentication(userId, email, role);
//
//            // SecurityContext에 인증 정보 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        // 다음 필터로 요청 전달
//        filterChain.doFilter(request, response);
//    }
//
//    // ?
//
//    // JWT 토큰을 요청 헤더에서 가져오는 메서드
//    private String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // "Bearer " 이후의 실제 토큰 값 추출
//        }
//        return null;
//    }
//
//    // 토큰에서 Claims 정보 추출
//    private Claims getClaimsFromToken(String token) {
//        return Jwts.parserBuilder()
//            .setSigningKey(key)
//            .build()
//            .parseClaimsJws(token)
//            .getBody();
//    }
//
//    // Authentication 객체 생성 (사용자 정보와 권한 설정)
//    private Authentication createAuthentication(Long userId, String email, String role) {
//        // 권한 설정
//        var authorities = AuthorityUtils.createAuthorityList(role);
//        // Authentication 객체 생성
//        return new CustomAuthentication(userId, email, authorities);
//    }
//}
