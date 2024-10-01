package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.response.GetUserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
@Component
public interface UserClient {

    // TODO : 내부 API 생성 필요
    @GetMapping("/users/{userId}")
    GetUserRes getUser(Long userId);

    @GetMapping("/users/{nickname}/nickname")
    GetUserRes searchNickname(String nickname);
}
