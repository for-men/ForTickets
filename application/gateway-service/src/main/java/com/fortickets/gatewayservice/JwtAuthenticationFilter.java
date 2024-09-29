package com.fortickets.gatewayservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


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
        // ToDo 토큰 검증s
        try {
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            SecretKey key = getSecretKey();
            Claims claims = getUserInfoFromToken(token, key);
            Integer userId = claims.get("userId", Integer.class);
            String email = claims.get("email", String.class);
            String role = claims.get("auth").toString();
            addHeadersToRequest(exchange, userId, email, role);
            return chain.filter(exchange);

        } catch (ExpiredJwtException e) {
            return exchange.getResponse().writeWith(Mono.just(excepctionMessage(exchange, "토큰이 만료되었습니다.")));
        } catch (UnsupportedJwtException e) {
            return exchange.getResponse().writeWith(Mono.just(excepctionMessage(exchange, "지원되지 않는 형식의 JWT입니다.")));
        } catch (MalformedJwtException e) {
            return exchange.getResponse().writeWith(Mono.just(excepctionMessage(exchange, "JWT의 구조가 손상되었거나 올바르지 않습니다.")));
        } catch (SignatureException e) {
            return exchange.getResponse().writeWith(Mono.just(excepctionMessage(exchange, "JWT 서명이 유효하지 않습니다.")));
        } catch (IllegalArgumentException e) {
            return exchange.getResponse().writeWith(Mono.just(excepctionMessage(exchange, "입력값이 잘못되었습니다.")));
        }

    }

    /**
     * 해더에서 토큰 얻기
     *
     * @param exchange
     * @return
     */
    private String getJwtTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 로그인  회원가입의 경우는 토큰이 없어서 통과 시켜야함
     *
     * @param path 요청 주소
     * @return
     */
    private boolean isAuthorizationPassRequest(String path) {
        return path.startsWith("/auth/login") || path.startsWith("/auth/sign-up");
    }

    /**
     * 비밀 키
     *
     * @return
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    /**
     * 검큰 검증 및 검큰에서 정보 추출
     *
     * @param token 해더에서 얻은 토큰
     * @param key   설정에서 가져오는 비밀키
     * @return
     * @throws ExpiredJwtException      토큰이 만료된 경우.
     * @throws UnsupportedJwtException  지원되지 않는 형식의 JWT인 경우.
     * @throws MalformedJwtException    JWT의 구조가 손상되었거나 올바르지 않은 경우.
     * @throws SignatureException       JWT 서명이 유효하지 않은 경우.
     * @throws IllegalArgumentException 입력값이 잘못된 경우.
     */
    public Claims getUserInfoFromToken(String token, SecretKey key)
        throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /**
     * 정보 해더에 넣기
     *
     * @param exchange
     * @param userId
     * @param email
     * @param role
     */
    private void addHeadersToRequest(ServerWebExchange exchange, Integer userId, String email, String role) {
        exchange.getRequest().mutate()
            .header("X-User-Id", userId.toString())
            .header("X-Email", email)
            .header("X-Role", role)
            .build();
    }

    private DataBuffer excepctionMessage(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        // 응답의 Content-Type을 JSON으로 설정
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 응답 바디에 넣을 메시지
        String responseBody = "{\"error\": \"" + message + "\"}";

        // 응답 바디에 데이터를 쓰기 위한 DataBuffer 생성
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().bufferFactory().wrap(bytes);
    }
}
