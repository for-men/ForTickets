package com.fortickets.concertservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.fortickets.concertservice.application.dto.CreateStageRes;
import com.fortickets.concertservice.application.dto.response.GetStageRes;
import com.fortickets.concertservice.domain.entity.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface StageMapper {

  CreateStageRes toCreateStageRes(Stage stage);

  GetStageRes toGetStageRes(Stage stage);

}
