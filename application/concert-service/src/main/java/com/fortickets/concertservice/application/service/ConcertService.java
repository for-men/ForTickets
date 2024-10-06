package com.fortickets.concertservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.mapper.ConcertMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import com.fortickets.exception.GlobalException;
import java.util.List;
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


  public GetConcertRes getConcert(Long concertId) {
    Concert concert = concertRepository.findById(concertId).orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
    return concertMapper.toGetConcertRes(concert);
  }

  public List<GetConcertDetailRes> getConcertBySeller(Long userId) {
    List<Concert> concertList = concertRepository.findByUserId(userId);
    return concertMapper.toGetConcertDetailResList(concertList);
  }
}
