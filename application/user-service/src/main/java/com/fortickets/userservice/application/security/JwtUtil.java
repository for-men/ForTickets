package com.fortickets.userservice.application.security;

import com.fortickets.userservice.domain.entity.UserRoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // 사용자 ID 값의 KEY
    public static final String USER_ID_KEY = "userId";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Value("${spring.application.name}")
    private String issuer;
    @Value("${service.jwt.secret-key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(Long userId, String email, UserRoleEnum role) {
        Date date = new Date();

        // JWT Claims 확인 로그 추가
        log.info("JWT Claims - userId: {}, email: {}, role: {}", userId, email, role);

        return BEARER_PREFIX +
            Jwts.builder()
                .claim(USER_ID_KEY, userId) // 사용자 식별자값(ID)
                .claim("email", email)
                .claim(AUTHORIZATION_KEY, role)
                .issuer(issuer)
                .setIssuedAt(date) // 발급일
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                .signWith(key, signatureAlgorithm)
                .compact();
    }
}
