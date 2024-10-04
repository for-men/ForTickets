package com.fortickets.concertservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleSeatRes;
import com.fortickets.concertservice.domain.entity.Schedule;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface ScheduleMapper {

  CreateScheduleRes toCreateScheduleRes(Schedule schedule);

  GetScheduleSeatRes toGetScheduleRes(Schedule schedule, List<String> seatList);

}
