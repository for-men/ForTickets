package com.fortickets.concertservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.request.UpdateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
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

  public Page<GetConcertsRes> getAllConcerts(Pageable pageable) {
    Page<Concert> concertList = concertRepository.findAll(pageable);
    return concertList.map(concertMapper::toGetConcertsRes);
  }

  public GetConcertRes getConcertById(Long concertId) {
    Concert concert = getConcertUtil(concertId);
    return concertMapper.toGetConcertRes(concert);
  }


  @Transactional
  public void updateConcertById(Long concertId, UpdateConcertReq updateConcertReq) {
   Concert concert = getConcertUtil(concertId);
    changeConcert(updateConcertReq, concert);
  }

  @Transactional
  public void deleteConcertById(Long concertId, String email) {
    Concert concert = getConcertUtil(concertId);
    concert.delete(email);
  }


  private static void changeConcert(UpdateConcertReq updateConcertReq, Concert concert) {
    if(updateConcertReq.concertImage() !=null)
      concert.changeImage(updateConcertReq.concertImage());
    if(updateConcertReq.concertName() != null)
      concert.changeName(updateConcertReq.concertName());
    if(updateConcertReq.runtime() != null)
      concert.changeRuntime(updateConcertReq.runtime());
    if(updateConcertReq.startDate() != null)
      concert.changeStartDate(updateConcertReq.startDate());
    if(updateConcertReq.endDate() != null)
      concert.changeEndDate(updateConcertReq.endDate());
    if(updateConcertReq.price() != null)
      concert.changePrice(updateConcertReq.price());
  }

  private Concert getConcertUtil(Long concertId) {
    return concertRepository.findById(concertId)
        .orElseThrow(()-> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
  }


  public GetConcertRes getConcert(Long concertId) {
    Concert concert = concertRepository.findById(concertId).orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
    return concertMapper.toGetConcertRes(concert);
  }

  public List<GetConcertDetailRes> getConcertBySeller(Long userId) {
    List<Concert> concertList = concertRepository.findByUserId(userId);
    return concertMapper.toGetConcertDetailResList(concertList);
  }

  public List<GetConcertDetailRes> searchConcert(Long userId, String concertName) {
    List<Concert> concertList = concertRepository.findByUserIdAndConcertNameContaining(userId, concertName);
    return concertMapper.toGetConcertResList(concertList);
  }

  public List<GetConcertDetailRes> searchConcertName(String concertName) {
    List<Concert> concertList = concertRepository.findByConcertNameContaining(concertName);
    return concertMapper.toGetConcertResList(concertList);
  }
}
