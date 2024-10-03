package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.CreateStageRes;
import com.fortickets.concertservice.application.dto.request.CreateStageReq;
import com.fortickets.concertservice.application.dto.response.GetStageRes;
import com.fortickets.concertservice.application.service.StageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {
  private final StageService stageService;

  @PostMapping
  public CommonResponse<CreateStageRes> createStage(@RequestBody CreateStageReq createStageReq) {
    // Role  MANAGER 확인 필요
    return CommonResponse.success(stageService.createStage(createStageReq));
  }

  @GetMapping
  public CommonResponse<List<GetStageRes>> getStages() {
    return CommonResponse.success(stageService.getAllStage());
  }

  @GetMapping("/{stageId}")
  public CommonResponse<GetStageRes> getStage(@PathVariable("stageId") Long stageId) {
    return CommonResponse.success(stageService.getStage(stageId));
  }

}
