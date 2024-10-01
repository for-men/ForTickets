package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.res.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name = "concert-service")
@Component
public interface ConcertClient {

    // TODO: 내부 API 생성 필요
    GetScheduleRes getSchedule(Long scheduleId);

    GetConcertRes searchConcertName(String concertName);
}
