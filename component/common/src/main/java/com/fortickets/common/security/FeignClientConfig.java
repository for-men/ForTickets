package com.fortickets.common.security;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // SecurityContext에서 인증 정보 가져오기
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof UsernamePasswordAuthenticationToken token) {
                // 사용자 정보 추출
                Long userId = (Long) token.getPrincipal();  // userId는 Principal에 저장됨
                String email = (String) token.getCredentials();  // email은 Credentials에 저장됨
                String role = token.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);

                // 로그 추가
                log.info("Intercepting request with headers: X-User-Id={}, X-Email={}, X-Role={}", userId, email, role);

                // 헤더에 사용자 정보 추가
                template.header("X-User-Id", String.valueOf(userId));
                template.header("X-Email", email);
                if (role != null) {
                    template.header("X-Role", role);
                }
            } else {
                log.warn("Authentication is not of type UsernamePasswordAuthenticationToken: {}", authentication);
            }
        };
    }
}
