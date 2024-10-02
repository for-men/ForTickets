package com.fortickets.concertservice.application.service;

import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.mapper.ConcertMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertService {
  private final ConcertRepository concertRepository;
  private final ConcertMapper concertMapper;


  public CreateConcertRes createConcert(CreateConcertReq createConcertReq,Long userId) {
    Concert concert = createConcertReq.toEntity(userId);
    concert = concertRepository.save(concert);

    return concertMapper.toCreateConcertRes(concert);
  }

}
