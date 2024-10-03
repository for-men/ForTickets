package com.fortickets.concertservice.application.service;

import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.mapper.ConcertMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
  private final ConcertRepository concertRepository;
  private final ConcertMapper concertMapper;

  @Transactional
  public CreateConcertRes createConcert(CreateConcertReq createConcertReq,Long userId) {
    Concert concert = createConcertReq.toEntity(userId);
    concert = concertRepository.save(concert);

    return concertMapper.toCreateConcertRes(concert);
  }

  public Page<GetConcertRes> getAllConcerts(Pageable pageable) {
    Page<Concert> concertList = concertRepository.findAll(pageable);
    return concertList.map(concertMapper::toGetConcertRes);
  }


}
