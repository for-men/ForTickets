package com.fortickets.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Order(1)
@Slf4j
@Configuration
@EnableWebSecurity // Spring Security 사용을 위한 설정
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자를 생성하여 인증 구성을 제공
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilterForCommon jwtAuthenticationFilterForCommon() {
        return new JwtAuthenticationFilterForCommon();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 설정
        http.csrf((csrf) -> csrf.disable());

        // Session 비활성화
        http.sessionManagement((sessionManagement) ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 요청 권한 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
            authorizeHttpRequests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                .requestMatchers("/auth/**").permitAll() // '/api/auth/'로 시작하는 요청 모두 접근 허가
                .requestMatchers("/actuator/**").permitAll() // '/actuator/'로 시작하는 요청 모두 접근 허가
                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 인증 실패 핸들러와 권한 없음 핸들러 등록
        http.exceptionHandling(exceptionHandling ->
            exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthenticationFilterForCommon(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 인증 실패 (401) 처리 핸들러
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("인증 실패: {}", authException.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401 상태 설정
            response.getWriter().write("인증이 필요합니다.");
            response.getWriter().flush(); // Flush the response writer
        };
    }

    // 권한 없음 (403) 처리 핸들러
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn("권한 부족: {}", accessDeniedException.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());  // 403 상태 설정
            response.getWriter().write("접근 권한이 없습니다.");
            response.getWriter().flush(); // Flush the response writer
        };
    }
}
