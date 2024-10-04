package com.fortickets.concertservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.concertservice.application.dto.response.CreateStageRes;
import com.fortickets.concertservice.application.dto.request.CreateStageReq;
import com.fortickets.concertservice.application.dto.response.GetStageRes;
import com.fortickets.concertservice.domain.entity.Stage;
import com.fortickets.concertservice.domain.mapper.StageMapper;
import com.fortickets.concertservice.domain.repository.StageRepository;
import com.fortickets.exception.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StageService {

  private final StageRepository stageRepository;
  private final StageMapper stageMapper;

  @Transactional
  public CreateStageRes createStage(CreateStageReq createStageReq) {
    Stage stage = createStageReq.toEntity();
    return stageMapper.toCreateStageRes(stageRepository.save(stage));
  }

  public List<GetStageRes> getAllStage() {
    List<Stage> stages = stageRepository.findAll();
    return stages.stream().map(stageMapper::toGetStageRes).toList();
  }

  public GetStageRes getStage(Long stageId) {
    Stage stage = stageRepository.findById(stageId)
        .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_STAGE));
    return stageMapper.toGetStageRes(stage);
  }


}
