package com.fortickets.orderservice.application.client;

import com.fortickets.orderservice.application.dto.response.GetScheduleRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name = "concert-service")
@Component
public class ConcertClient {

    public GetScheduleRes getSchedule(Long scheduleId) {
        return new GetScheduleRes();
    }
}
