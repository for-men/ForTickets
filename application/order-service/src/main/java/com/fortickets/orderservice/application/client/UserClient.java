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

    @GetMapping("/users/{userId}")
    GetUserRes getUser(@PathVariable(value = "userId") Long userId);

    @GetMapping("/users/{nickname}/nickname")
    List<GetUserRes> searchNickname(@PathVariable(value = "nickname") String nickname);
}
