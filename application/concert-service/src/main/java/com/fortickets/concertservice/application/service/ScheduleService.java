package com.fortickets.concertservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.concertservice.application.client.BookingClient;
import com.fortickets.concertservice.application.dto.request.CreateScheduleReq;
import com.fortickets.concertservice.application.dto.request.UpdateScheduleReq;
import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleSeatRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.Schedule;
import com.fortickets.concertservice.domain.entity.Stage;
import com.fortickets.concertservice.domain.mapper.ScheduleMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import com.fortickets.concertservice.domain.repository.ScheduleRepository;
import com.fortickets.concertservice.domain.repository.StageRepository;
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

    // 스케줄 생성
    @Transactional
    public CreateScheduleRes createSchedule(Long userId, CreateScheduleReq createScheduleReq) {
        // 해당하는 콘서트 불러오기
        Concert concert = concertRepository.findById(createScheduleReq.concertId())
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
        // 해당하는 공연장 불러오기
        Stage stage = getStage(createScheduleReq.stageId());
        // 유저의 공연인지 확인
        if (!concert.getUserId().equals(userId)) {
            throw new GlobalException(ErrorCase.NOT_PERMITTED_TO_ADD_SCHEDULE);
        }

        // 공연 시작 날짜 및 종료 날짜 검증
        if (createScheduleReq.concertDate().isBefore(concert.getStartDate())) {
            throw new GlobalException(ErrorCase.SCHEDULE_START_DATE_TOO_EARLY); // 시작 날짜가 너무 이르다는 오류
        }

        if (createScheduleReq.concertDate().isAfter(concert.getEndDate())) {
            throw new GlobalException(ErrorCase.SCHEDULE_START_DATE_TOO_LATE); // 시작 날짜가 너무 늦다는 오류
        }

        Schedule schedule = createScheduleReq.toEntity(concert, stage);
        return scheduleMapper.toCreateScheduleRes(scheduleRepository.save(schedule));
    }

    // 스케줄 단건 조회
    public GetScheduleSeatRes getScheduleById(Long scheduleId) {
        // 스케줄에 대한 공연 정보 가져오기
        // 스케줄에 대한 공연장 정보 가져오기
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_SCHEDULE));

        // Order Service 가 실행중이지 않을시 500에러 발생
        List<String> seatList;
        try {
            // 예매 서비스 호출
            seatList = bookingClient.getSeatsWithBooking(scheduleId).getData();
        } catch (Exception e) {
            throw new GlobalException(ErrorCase.SYSTEM_ERROR);
        }

        return scheduleMapper.toGetScheduleSeatRes(schedule, seatList);
    }

    // 스케줄 상세 조회
    public GetScheduleDetailRes getScheduleDetail(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_SCHEDULE));

        return scheduleMapper.toGetScheduleDetailRes(schedule);

    }
    // 스케줄 수정
    @Transactional
    public void updateScheduleById(Long getUserId, String role, Long scheduleId, UpdateScheduleReq updateScheduleReq) {
        Schedule schedule = getSchedule(scheduleId);
        Concert concert = schedule.getConcert();
        if (!concert.getUserId().equals(getUserId) || !role.equals("MANAGER")) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }

        if (updateScheduleReq.concertDate() != null) {
            if (updateScheduleReq.concertDate().isBefore(concert.getStartDate())) {
                throw new GlobalException(ErrorCase.SCHEDULE_START_DATE_TOO_EARLY);
            }

            if (updateScheduleReq.concertDate().isAfter(concert.getEndDate())) {
                throw new GlobalException(ErrorCase.SCHEDULE_START_DATE_TOO_LATE);
            }
            schedule.changeConcertDate(updateScheduleReq.concertDate());
        }

        changeSchedule(updateScheduleReq, schedule);
    }

    // 스케줄 삭제
    @Transactional
    public void deleteScheduleById(Long getUserId, String role, String email, Long scheduleId) {

        Schedule schedule = getSchedule(scheduleId);
        Concert concert = schedule.getConcert();
        if (!concert.getUserId().equals(getUserId) || !role.equals("MANAGER")) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }
        schedule.delete(email);
    }

    private void changeSchedule(UpdateScheduleReq updateScheduleReq, Schedule schedule) {
        if (updateScheduleReq.stageId() != null) {
            schedule.changeStage(getStage(updateScheduleReq.stageId()));
        }
        if (updateScheduleReq.concertDate() != null) {
            schedule.changeConcertDate(updateScheduleReq.concertDate());
        }
        if (updateScheduleReq.concertTime() != null) {
            schedule.changeConcertTime(updateScheduleReq.concertTime());
        }
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_SCHEDULE));
    }

    private Stage getStage(Long stageId) {
        return stageRepository.findById(stageId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_STAGE));
    }
}
