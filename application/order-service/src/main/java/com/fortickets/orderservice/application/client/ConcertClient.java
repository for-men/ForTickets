package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.res.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "concert-service")
@Component
public interface ConcertClient {

    // TODO: 내부 API 생성 필요
    @GetMapping("/schedules/{scheduleId}")
    GetScheduleRes getSchedule(@PathVariable Long scheduleId);

    @GetMapping("/concerts/{concertName}")
    GetConcertRes searchConcertName(@PathVariable String concertName);
}
