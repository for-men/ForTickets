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
import com.fortickets.common.exception.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertMapper concertMapper;

    // 공연 생성
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @Transactional
    public CreateConcertRes createConcert(CreateConcertReq createConcertReq,Long userId) {
        Concert concert = createConcertReq.toEntity(userId);
        concert = concertRepository.save(concert);

        return concertMapper.toCreateConcertRes(concert);
    }

    // 모든 공연 조회
    public Page<GetConcertsRes> getAllConcerts(Pageable pageable) {
        Page<Concert> concertList = concertRepository.findAll(pageable);
        return concertList.map(concertMapper::toGetConcertsRes);
    }

    // 특정 공연 조회
    public GetConcertRes getConcertById(Long concertId) {
        Concert concert = getConcertUtil(concertId);
        return concertMapper.toGetConcertRes(concert);
    }

    // 특정 공연 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @Transactional
    public void updateConcertById(Long concertId, UpdateConcertReq updateConcertReq) {
       Concert concert = getConcertUtil(concertId);
        changeConcert(updateConcertReq, concert);
    }

    // 특정 공연 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @Transactional
    public void deleteConcertById(Long concertId, String email) {
        Concert concert = getConcertUtil(concertId);
        concert.delete(email);
    }

    // 콘서트 부분 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
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

    // 콘서트 조회 유틸
    private Concert getConcertUtil(Long concertId) {
        return concertRepository.findById(concertId)
            .orElseThrow(()-> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
    }

    // 콘서트 ID로 콘서트 조회
    public GetConcertRes getConcert(Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
        return concertMapper.toGetConcertRes(concert);
    }

    // 해당 유저가 등록한 모든 콘서트 조회
    public List<GetConcertDetailRes> getConcertBySeller(Long userId) {
        List<Concert> concertList = concertRepository.findByUserId(userId);
        return concertMapper.toGetConcertDetailResList(concertList);
    }

    // 특정 판매자가 등록한 콘서트 중 해당 문자를 제목에 포함한 콘서트 조회
    public List<GetConcertDetailRes> searchConcert(Long userId, String concertName) {
        List<Concert> concertList = concertRepository.findByUserIdAndConcertNameContaining(userId, concertName);
        return concertMapper.toGetConcertResList(concertList);
    }

    // 해당 문자를 포함한 콘서트 조회
    public List<GetConcertDetailRes> searchConcertName(String concertName) {
        List<Concert> concertList = concertRepository.findByConcertNameContaining(concertName);
        return concertMapper.toGetConcertResList(concertList);
    }
}
