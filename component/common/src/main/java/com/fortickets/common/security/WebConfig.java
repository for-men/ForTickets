package com.fortickets.common.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ArgumentResolver를 빈으로 등록
    @Bean
    public UseAuthHandlerMethodArgumentResolver getUseAuthHandlerMethodArgumentResolver() {
        return new UseAuthHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(getUseAuthHandlerMethodArgumentResolver());
    }
}
