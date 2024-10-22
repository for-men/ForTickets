package com.fortickets.gatewayservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest; // 올바른 패키지
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ToDo 요청에서 요청 주소 빼기
        String path = exchange.getRequest().getURI().getPath();

        // ToDO 로그인 및 회원가입 경로는 토큰 검증을 통과합니다.
        if (isAuthorizationPassRequest(path)) {
            return chain.filter(exchange);
        }
        // ToDo 해더에서 jwt 가져오기
        String token = getJwtTokenFromHeader(exchange);
        if (token == null) {
            return unauthorizedResponse(exchange, "JWT 토큰이 없습니다.");
        }

        // ToDo 토큰 검증
        try {
            SecretKey key = getSecretKey();
            Claims claims = getUserInfoFromToken(token, key);

            // 검증된 사용자 정보 출력
//            log.info("JWT 토큰에서 추출한 사용자 정보: userId = {}, email = {}, role = {}",
//                claims.get("userId"), claims.get("email"), claims.get("role"));

            Integer userId = claims.get("userId", Integer.class);
            String email = claims.get("email", String.class);
            String role = claims.get("role").toString();

            // 헤더에 사용자 정보 추가
            ServerHttpRequest newRequest = addHeadersToRequest(exchange, userId, email, role);
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (ExpiredJwtException e) {
            return unauthorizedResponse(exchange, "토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            return unauthorizedResponse(exchange, "지원되지 않는 형식의 JWT입니다.");
        } catch (MalformedJwtException e) {
            return unauthorizedResponse(exchange, "JWT의 구조가 손상되었거나 올바르지 않습니다.");
        } catch (SignatureException e) {
            return unauthorizedResponse(exchange, "JWT 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            return unauthorizedResponse(exchange, "입력값이 잘못되었습니다.");
        }
    }

    // 헤더에서 토큰 추출
    private String getJwtTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // 회원가입 및 로그인 필터 통과
    private boolean isAuthorizationPassRequest(String path) {
        return path.startsWith("/auth/login") || path.startsWith("/auth/sign-up");
    }

    // 시크릿키 생성
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    // JWT 토큰에서 사용자 정보 추출 및 검증
    public Claims getUserInfoFromToken(String token, SecretKey key) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // JWT 검증 실패 시 응답 처리
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = "{\"error\": \"" + message + "\"}";
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    // 헤더에 사용자 정보 추가
    private ServerHttpRequest addHeadersToRequest(ServerWebExchange exchange, Integer userId, String email, String role) {
        return exchange.getRequest().mutate()
            .header("X-User-Id", userId.toString())
            .header("X-Email", email)
            .header("X-Role", role)
            .build();
    }
}