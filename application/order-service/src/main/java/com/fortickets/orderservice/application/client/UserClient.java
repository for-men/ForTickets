package com.fortickets.orderservice.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name = "user-service")
@Component
public interface UserClient {

}
