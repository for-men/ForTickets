package com.fortickets.concertservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.Schedule;
import com.fortickets.concertservice.domain.entity.Stage;
import com.fortickets.concertservice.domain.mapper.ScheduleMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import com.fortickets.concertservice.domain.repository.ScheduleRepository;
import com.fortickets.concertservice.domain.repository.StageRepository;
import com.fortickets.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
  private final ScheduleRepository scheduleRepository;
  private final StageRepository stageRepository;
  private final ScheduleMapper scheduleMapper;
  private final ConcertRepository concertRepository;

  @Transactional
  public CreateScheduleRes createSchedule(CreateScheduleReq createScheduleReq, Long userId) {
    // 해당하는 콘서트 불러오기
    Concert concert = concertRepository.findById(createScheduleReq.concertId())
        .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
    // 해당하는 공연장 불러오기
    Stage stage = stageRepository.findById(createScheduleReq.stageId())
        .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_STAGE));
    // 유저의 공연인지 확인
    if(!concert.getUserId().equals(userId)) {
      throw new GlobalException(ErrorCase.NOT_PERMITTED_TO_ADD_SCHEDULE);
    }
    Schedule schedule = createScheduleReq.toEntity(concert,stage);
    return scheduleMapper.toCreateScheduleRes(scheduleRepository.save(schedule));
  }

}
