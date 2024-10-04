package com.fortickets.concertservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.fortickets.concertservice.application.dto.response.CreateScheduleRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.concertservice.application.dto.response.GetScheduleRes;
import com.fortickets.concertservice.domain.entity.Schedule;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface ScheduleMapper {

  CreateScheduleRes toCreateScheduleRes(Schedule schedule);

  GetScheduleRes toGetScheduleRes(Schedule schedule, List<String> seatList);

  @Mapping(target = "concertName", source = "schedule.concert.concertName")
  @Mapping(target = "runtime", source = "schedule.concert.runtime")
  @Mapping(target = "location", source = "schedule.stage.location")
  @Mapping(target = "stageName", source = "schedule.stage.name")
  @Mapping(target = "concertId", source = "schedule.concert.id")
  GetScheduleDetailRes toGetScheduleDetailRes(Schedule schedule);

}
