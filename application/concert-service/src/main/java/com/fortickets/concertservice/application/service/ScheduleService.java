package com.fortickets.concertservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.concertservice.application.client.BookingClient;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.Schedule;
import com.fortickets.concertservice.domain.entity.Stage;
import com.fortickets.concertservice.domain.mapper.ScheduleMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import com.fortickets.concertservice.domain.repository.ScheduleRepository;
import com.fortickets.concertservice.domain.repository.StageRepository;
import com.fortickets.exception.GlobalException;
import java.util.List;
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
  private final BookingClient bookingClient;

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

  public GetScheduleRes getSchedule(Long scheduleId) {
    // 스케줄에 대한 공연 정보 가져오기
    // 스케줄에 대한 공연장 정보 가져오기
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_SCHEDULE));

    // 스케줄 아이디를 통해 예매 조회 후 예매 상태 확인 및 조건에 맞는 해당 좌석 불러오기
    // 조건 : status가 PENDING , CONFIRMED
    List<String> seatList = bookingClient.getSeatsWithBooking(scheduleId).getData();

    return scheduleMapper.toGetScheduleRes(schedule,seatList);
  }

}
