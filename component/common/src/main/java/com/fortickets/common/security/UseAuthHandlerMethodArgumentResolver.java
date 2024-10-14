package com.fortickets.common.security;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UseAuthHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {


    // 일종의 벨리데이션
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        UseAuth annotaion = parameter.getParameterAnnotation(UseAuth.class);
        if (annotaion == null) return false;
        return parameter.getParameterType().equals(CustomUser.class);
    }

    // 직접 데이터를 넣어주는 역할
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String userId = webRequest.getHeader("X-User-Id");
        String email = webRequest.getHeader("X-Email");
        String role = webRequest.getHeader("X-Role");

        if(userId == null) {
            throw new Exception();
        }

        return new CustomUser(Long.valueOf(userId), email, role);
    }
}
