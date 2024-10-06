package com.fortickets.userservice.application;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "order-service") // 주문 서비스의 URL을 설정합니다.
public interface OrderClient {

//    @GetMapping("/bookings/hello")
//    String hello();

}
