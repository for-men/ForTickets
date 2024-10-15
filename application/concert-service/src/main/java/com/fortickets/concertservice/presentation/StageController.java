package com.fortickets.concertservice.presentation;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateStageReq;
import com.fortickets.concertservice.application.dto.request.UpdateStageReq;
import com.fortickets.concertservice.application.dto.response.CreateStageRes;
import com.fortickets.concertservice.application.dto.response.GetStageRes;
import com.fortickets.concertservice.application.service.StageService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/stages")
public class StageController {

    private final StageService stageService;

    // 공연장 생성
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public CommonResponse<CreateStageRes> createStage(@Valid @RequestBody CreateStageReq createStageReq) {
        return CommonResponse.success(stageService.createStage(createStageReq));
    }

    // 공연장 전체 리스트 조회
    @GetMapping
    public CommonResponse<List<GetStageRes>> getStages() {
        return CommonResponse.success(stageService.getAllStage());
    }

    // 특정 공연장 조회
    @GetMapping("/{stageId}")
    public CommonResponse<GetStageRes> getStageById(@PathVariable("stageId") Long stageId) {
        return CommonResponse.success(stageService.getStageById(stageId));
    }

    // 공연장 수정
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{stageId}")
    public CommonResponse<GetStageRes> updateStageById(@PathVariable("stageId") Long stageId, @Valid @RequestBody UpdateStageReq updateStageReq){
        stageService.updateStageById(stageId,updateStageReq);
        return CommonResponse.success(stageService.getStageById(stageId));
    }

    // 공연장 삭제
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{stageId}")
    public CommonResponse deleteStageById(@UseAuth CustomUser customUser ,@PathVariable("stageId") Long stageId) {
        stageService.deleteStageById(customUser.getEmail(), stageId);
        return CommonResponse.success();
    }
}
