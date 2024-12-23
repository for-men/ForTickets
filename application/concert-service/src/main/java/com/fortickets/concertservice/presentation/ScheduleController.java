package com.fortickets.concertservice.presentation;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 스케줄 생성
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PostMapping
    public CommonResponse<CreateScheduleRes> createSchedule(
        @UseAuth CustomUser customUser,
        @Valid @RequestBody CreateScheduleReq createScheduleReq) {
        return CommonResponse.success(scheduleService.createSchedule(customUser.getUserId(), createScheduleReq));
    }

    // 스케줄 단건조회
    @GetMapping("/{scheduleId}")
    public CommonResponse<GetScheduleSeatRes> getScheduleById(@PathVariable("scheduleId") Long scheduleId) {
        return CommonResponse.success(scheduleService.getScheduleById(scheduleId));
    }

    // 스케줄 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PatchMapping("/{scheduleId}")
    public CommonResponse<GetScheduleSeatRes> updateScheduleById(
        @UseAuth CustomUser customUser,
        @PathVariable("scheduleId") Long scheduleId,
        @Valid @RequestBody UpdateScheduleReq updateScheduleReq) {
        scheduleService.updateScheduleById(customUser.getUserId(), customUser.getRole(), scheduleId, updateScheduleReq);
        return CommonResponse.success(scheduleService.getScheduleById(scheduleId));
    }

    // 스케줄 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @DeleteMapping("/{scheduleId}")
    public CommonResponse<CommonEmptyRes> deleteScheduleById(
        @UseAuth CustomUser customUser,
        @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.deleteScheduleById(customUser.getUserId(), customUser.getRole(), customUser.getEmail(), scheduleId);
        return CommonResponse.success();
    }

    // 스케줄 상세 조회
    @GetMapping("/{scheduleId}/detail")
    public GetScheduleDetailRes getScheduleDetail(@PathVariable Long scheduleId) {
        return scheduleService.getScheduleDetail(scheduleId);
    }
}
