package com.fortickets.userservice.application.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity // Spring Security 사용을 위한 설정
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자를 생성하여 인증 구성을 제공
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public JwtAuthenticationFilterForCommon jwtAuthenticationFilterForCommon() {
//        return new JwtAuthenticationFilterForCommon();
//    }

    @Bean
    public JwtAuthenticationFilterForUserService jwtAuthorizationFilterForUserService() {
        return new JwtAuthenticationFilterForUserService(userDetailsServiceImpl); // 필드 사용
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
                .requestMatchers("/auth/**").permitAll() // '/api/v1/auth/'로 시작하는 요청 모두 접근 허가
                .requestMatchers("/actuator/prometheus").permitAll()
                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 필터 관리
//        http.addFilterBefore(jwtAuthenticationFilterForCommon(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilterForUserService(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
