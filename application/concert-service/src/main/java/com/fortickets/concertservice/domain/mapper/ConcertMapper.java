package com.fortickets.concertservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
import com.fortickets.concertservice.domain.entity.Concert;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface ConcertMapper {
  CreateConcertRes toCreateConcertRes(Concert concert);

  // 공연 전체 조회 시 mapper
  GetConcertsRes toGetConcertsRes(Concert concert);

  // 공연 단일 조회 시 mapper
  GetConcertRes toGetConcertRes(Concert concert);

  List<GetConcertDetailRes> toGetConcertDetailResList(List<Concert> concertList);

    List<GetConcertDetailRes> toGetConcertResList(List<Concert> concertList);
}
