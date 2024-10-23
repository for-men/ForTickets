package com.fortickets.concertservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateStageReq;
import com.fortickets.concertservice.application.dto.request.UpdateStageReq;
import com.fortickets.concertservice.application.dto.response.CreateStageRes;
import com.fortickets.concertservice.application.dto.response.GetStageRes;
import com.fortickets.concertservice.domain.entity.Stage;
import com.fortickets.concertservice.domain.mapper.StageMapper;
import com.fortickets.concertservice.domain.repository.StageRepository;
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

    // 공연장 생성
    @Transactional
    public CreateStageRes createStage(CreateStageReq createStageReq) {
        Stage stage = createStageReq.toEntity();
        return stageMapper.toCreateStageRes(stageRepository.save(stage));
    }

    // 공연장 전체 리스트 조회
    public List<GetStageRes> getAllStage() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toGetStageRes).toList();
    }

    // 특정 공연장 조회
    public GetStageRes getStageById(Long stageId) {
        Stage stage = getStage(stageId);
        return stageMapper.toGetStageRes(stage);
    }

    // 공연장 수정
    @Transactional
    public void updateStageById(Long stageId, UpdateStageReq updateStageReq) {
        Stage stage = getStage(stageId);
        changeStage(updateStageReq, stage);
    }

    // 공연장 삭제
    @Transactional
    public void deleteStageById(String email, Long stageId) {
        Stage stage = getStage(stageId);
        stage.delete(email);
    }

    private Stage getStage(Long stageId) {
        return stageRepository.findById(stageId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_STAGE));
    }

    private static void changeStage(UpdateStageReq updateStageReq, Stage stage) {
        if (updateStageReq.name() != null) {
            stage.changeName(updateStageReq.name());
        }
        if (updateStageReq.location() != null) {
            stage.changeLocation(updateStageReq.location());
        }
        if (updateStageReq.row() != null) {
            stage.changeRow(updateStageReq.row());
        }
        if (updateStageReq.col() != null) {
            stage.changeCol(updateStageReq.col());
        }
    }
}
