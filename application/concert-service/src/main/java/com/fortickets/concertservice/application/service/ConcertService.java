package com.fortickets.concertservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.request.UpdateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.mapper.ConcertMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import com.fortickets.redis.RestPage;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    // 공연 생성
    @Transactional
    public CreateConcertRes createConcert(Long userId, CreateConcertReq createConcertReq) {
        Concert concert = createConcertReq.toEntity(userId);
        concert = concertRepository.save(concert);
        return concertMapper.toCreateConcertRes(concert);
    }

    // 모든 공연 조회
    @Cacheable(value = "concerts")
    public Page<GetConcertsRes> getAllConcerts(Pageable pageable) {
        Page<Concert> concertList = concertRepository.findAll(pageable);

        // Page<Concert>를 List<GetConcertsRes>로 변환
        List<GetConcertsRes> concertResponses = concertList.getContent()
            .stream()
            .map(concertMapper::toGetConcertsRes)
            .collect(Collectors.toList());

        // RestPage로 변환하여 반환
        return new RestPage<>(concertResponses, concertList.getNumber(), concertList.getSize(), concertList.getTotalElements());
    }

    // 특정 공연 조회
    @Cacheable(value = "concert")
    public GetConcertRes getConcertById(Long concertId) {
        Concert concert = getConcertUtil(concertId);
        return concertMapper.toGetConcertRes(concert);
    }

    // 특정 공연 수정
    @Transactional
    @CachePut(value = "concert", key = "#concertId")
    public GetConcertRes updateConcertById(Long userId, String role, Long concertId, UpdateConcertReq updateConcertReq) {
        Concert concert = getConcertUtil(concertId);
        // 관리자 또는 본인만 수정 가능
        validateAuthorization(role, userId, concert.getUserId());
        changeConcert(updateConcertReq, concert);
        return concertMapper.toGetConcertRes(concert);
    }

    // 특정 공연 삭제
    @Transactional
    @CacheEvict(value = "concert", key = "#concertId")
    public void deleteConcertById(Long userId, String role, String email, Long concertId) {
        Concert concert = getConcertUtil(concertId);

        // 관리자 또는 본인만 삭제 가능
        validateAuthorization(role, userId, concert.getUserId());

        concert.delete(email);
    }

    // 콘서트 부분 수정
    private static void changeConcert(UpdateConcertReq updateConcertReq, Concert concert) {
        if (updateConcertReq.concertImage() != null) {
            concert.changeImage(updateConcertReq.concertImage());
        }
        if (updateConcertReq.concertName() != null) {
            concert.changeName(updateConcertReq.concertName());
        }
        if (updateConcertReq.runtime() != null) {
            concert.changeRuntime(updateConcertReq.runtime());
        }
        if (updateConcertReq.startDate() != null) {
            concert.changeStartDate(updateConcertReq.startDate());
        }
        if (updateConcertReq.endDate() != null) {
            concert.changeEndDate(updateConcertReq.endDate());
        }
        if (updateConcertReq.price() != null) {
            concert.changePrice(updateConcertReq.price());
        }
    }

    // 콘서트 조회 유틸
    private Concert getConcertUtil(Long concertId) {
        return concertRepository.findById(concertId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_EXIST_CONCERT));
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
    public List<GetConcertRes> searchConcert(Long userId, String concertName) {
        List<Concert> concertList = concertRepository.findByUserIdAndConcertNameContaining(userId, concertName);
        return concertMapper.toGetConcertResList(concertList);
    }

    // 해당 문자를 포함한 콘서트 조회
    public List<GetConcertRes> searchConcertName(String concertName) {
        List<Concert> concertList = concertRepository.findByConcertNameContaining(concertName);
        return concertMapper.toGetConcertResList(concertList);
    }

    public List<GetConcertDetailRes> getConcertsByIds(List<Long> concertIds) {
        List<Concert> concertList = concertRepository.findByIdIn(concertIds);
        return concertMapper.toGetConcertDetailResList(concertList);

    }

    private void validateAuthorization(String role, Long userId, Long targetUserId) {
        if (!role.equals("ROLE_MANAGER") && !userId.equals(targetUserId)) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }
    }
}
