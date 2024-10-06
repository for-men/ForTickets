package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleRes;
import com.fortickets.concertservice.application.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
  private final ScheduleService scheduleService;

  @PostMapping
  public CommonResponse<CreateScheduleRes> createSchedule(
      @RequestHeader("X-User-Id") String userId,
      @RequestBody CreateScheduleReq createScheduleReq){
    return CommonResponse.success(scheduleService.createSchedule(createScheduleReq,Long.valueOf(userId)));
  }
  // 스케줄 단건조회
  @GetMapping("/{scheduleId}")
  public CommonResponse<GetScheduleRes> getSchedule(@PathVariable("scheduleId") Long scheduleId){
    return CommonResponse.success(scheduleService.getSchedule(scheduleId));
  }

  @GetMapping("/{scheduleId}/detail")
  public GetScheduleDetailRes getScheduleDetail(@PathVariable Long scheduleId) {
    return scheduleService.getScheduleDetail(scheduleId);
  }
}
