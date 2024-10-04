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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT에서 정보 추출")
public class JwtAuthenticationFilterForUserService extends OncePerRequestFilter {

    @Value("${service.jwt.secret-key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private SecretKey key;
    private final UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public JwtAuthenticationFilterForUserService(UserDetailsServiceImpl userDetailsService) {
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
                // 게이트웨이에서 이미 검증된 토큰이므로 추가 검증 없이 클레임 추출
                Claims claims = extractClaimsFromToken(tokenValue);
                log.info("Extracted Claims: {}", claims); // 추출된 사용자 정보 로그

                // 이메일을 클레임에서 직접 추출
                String email = claims.get(JwtUtil.USER_EMAIL, String.class); // USER_EMAIL을 사용하여 이메일 추출
                if (email == null || email.isEmpty()) {
                    log.error("Email is null or empty");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
                    return;
                }

                // 역할 추출 및 접두어 추가
                String role = claims.get(JwtUtil.USER_ROLE, String.class); // USER_ROLE을 사용하여 역할 추출
                if (role == null && role.isEmpty()) {
                    log.error("Role is null or empty");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
                    return;
                }

//                 인증 설정 여기서 헤더에 넣는 작업 추가
                setAuthentication(email, role); // 역할을 포함하여 인증 설정

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
        return Jwts.parserBuilder()
            .setSigningKey(key) // 서명 검증에 사용될 키 설정
            .build()
            .parseClaimsJws(token)
            .getBody(); // 클레임 부분을 가져옴
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
