package com.fortickets.concertservice.presentation;

import com.fortickets.common.util.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.request.UpdateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleSeatRes;
import com.fortickets.concertservice.application.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PostMapping
    public CommonResponse<CreateScheduleRes> createSchedule(
        @RequestHeader("X-User-Id") String userId,
        @Valid  @RequestBody CreateScheduleReq createScheduleReq){
        return CommonResponse.success(scheduleService.createSchedule(createScheduleReq,Long.valueOf(userId)));
    }

    // 스케줄 단건조회
    @GetMapping("/{scheduleId}")
    public CommonResponse<GetScheduleSeatRes> getScheduleById(@PathVariable("scheduleId") Long scheduleId){
        return CommonResponse.success(scheduleService.getScheduleById(scheduleId));
    }

    // 스케줄 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PatchMapping("/{scheduleId}")
    public CommonResponse<GetScheduleSeatRes> updateScheduleById(@PathVariable("scheduleId") Long scheduleId, @Valid @RequestBody UpdateScheduleReq updateScheduleReq){
        scheduleService.updateScheduleById(scheduleId,updateScheduleReq);
        return CommonResponse.success(scheduleService.getScheduleById(scheduleId));
    }

    // 스케줄 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @DeleteMapping("/{scheduleId}")
    public CommonResponse deleteScheduleById(@RequestHeader("X-Email") String email,@PathVariable("scheduleId") Long scheduleId){
        scheduleService.deleteScheduleById(scheduleId,email);
        return CommonResponse.success();
    }

    // 스케줄 상세 조회
    @GetMapping("/{scheduleId}/detail")
    public GetScheduleDetailRes getScheduleDetail(@PathVariable Long scheduleId) {
        return scheduleService.getScheduleDetail(scheduleId);
    }
}
