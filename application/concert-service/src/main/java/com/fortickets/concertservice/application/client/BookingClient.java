package com.fortickets.concertservice.application.client;

import com.fortickets.common.CommonResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
@Component
public interface BookingClient {
  // status가 PENDING , CONFIRMED 상태인 좌석
  @GetMapping("/bookings/seats/{scheduleId}")
  CommonResponse<List<String>> getSeatsWithBooking(@PathVariable("scheduleId") Long scheduleId);

}
