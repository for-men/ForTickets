package com.fortickets.concertservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.domain.entity.Concert;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface ConcertMapper {
  CreateConcertRes toCreateConcertRes(Concert concert);

}
