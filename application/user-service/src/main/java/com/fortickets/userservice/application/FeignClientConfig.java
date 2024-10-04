package com.fortickets.userservice.application;

import org.springframework.security.core.GrantedAuthority;
import com.fortickets.userservice.application.security.CustomAuthentication;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
            if (authentication instanceof CustomAuthentication customAuth) {
                // 사용자 정보 추출
                Long userId = customAuth.getUserId();
                String email = customAuth.getEmail();
                String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null); // 또는 필요한 방식으로 권한을 설정

                // 로그 추가
                log.info("Intercepting request with headers: X-User-Id={}, X-Email={}, X-Role={}", userId, email, role);

                // 추가적인 사용자 정보 헤더에 추가
                if (userId != null) {
                    template.header("X-User-Id", String.valueOf(userId));
                }
                if (email != null) {
                    template.header("X-Email", email);
                }
                if (role != null) {
                    template.header("X-Role", role);
                }
            }
        };
    }
}