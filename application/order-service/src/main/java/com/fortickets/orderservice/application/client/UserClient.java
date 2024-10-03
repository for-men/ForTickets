package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.response.GetUserRes;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
@Component
public interface UserClient {

    // TODO : 내부 API 생성 필요
    @GetMapping("/users/{userId}")
    GetUserRes getUser(@PathVariable Long userId);

    @GetMapping("/users/{nickname}/nickname")
    List<GetUserRes> searchNickname(@PathVariable String nickname);
}
