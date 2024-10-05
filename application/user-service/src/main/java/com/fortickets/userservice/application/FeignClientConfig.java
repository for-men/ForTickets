package com.fortickets.userservice.application;

import com.fortickets.userservice.application.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import com.fortickets.userservice.application.security.CustomAuthentication;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            if (authentication instanceof UsernamePasswordAuthenticationToken userAuth) {
                // 사용자 정보 추출
                Object principal = userAuth.getPrincipal();
                String email = principal instanceof UserDetailsImpl userDetails
                    ? userDetails.getUsername() // email 가져오기
                    : null;

                Long userId = principal instanceof UserDetailsImpl userDetailsImpl
                    ? userDetailsImpl.getUserId() // userId 가져오기
                    : null;

                String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null); // 권한 가져오기

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