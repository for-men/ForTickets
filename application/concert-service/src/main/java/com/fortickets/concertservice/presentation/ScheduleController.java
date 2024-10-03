package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
  private final ScheduleService scheduleService;

  @PostMapping
  public CommonResponse<CreateScheduleRes> createSchedule(@RequestBody CreateScheduleReq createScheduleReq){
    //  인증을 거친 UserId 필요
    Long userId = 1L;
    return CommonResponse.success(scheduleService.createSchedule(createScheduleReq,userId));
  }

}
