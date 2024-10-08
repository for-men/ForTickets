package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.security.FeignClientConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "concert-service", configuration = FeignClientConfig.class)
@Component
public interface ConcertClient {

    // TODO: 내부 API 생성 필요
    // Schedule(concert, stage) 정보 조회
    @GetMapping("/schedules/{scheduleId}/detail")
    GetScheduleDetailRes getScheduleDetail(@PathVariable Long scheduleId);

    // Concert 정보 조회 o
    @GetMapping("/concerts/{concertId}/detail")
    GetConcertRes getConcert(@PathVariable Long concertId);

    // Concert Name이 포함된 Concert 정보 조회
    @GetMapping("/concerts/{concertName}/search")
    List<GetConcertRes> searchConcertName(@PathVariable String concertName);

    // userId와 concertName이 포함된 Concert 정보 조회 o
    @GetMapping("/concerts/{userId}/{concertName}/search")
    List<GetConcertRes> searchConcert(@PathVariable Long userId, @PathVariable String concertName);

    // userId로 Concert 정보 조회 o
    @GetMapping("/concerts/{userId}/seller")
    List<GetConcertRes> getConcertBySeller(@PathVariable Long userId);
}
