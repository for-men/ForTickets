package com.fortickets.concertservice.presentation;

import com.fortickets.common.util.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.request.UpdateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
import com.fortickets.concertservice.application.service.ConcertService;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {

    private final ConcertService concertService;

    // 공연생성 , ROLE : SELLER , MANAGER
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PostMapping
    public CommonResponse<CreateConcertRes> createConcert(
            @RequestHeader("X-User-Id") String userId, @Valid
    @RequestBody CreateConcertReq createConcertReq) {
        var createConcertRes = concertService.createConcert(createConcertReq, Long.valueOf(userId));
        return CommonResponse.success(createConcertRes);
    }

    // 전체 공연 조회
    @GetMapping
    public CommonResponse<Page<GetConcertsRes>> getAllConcerts(Pageable pageable) {
        return CommonResponse.success(concertService.getAllConcerts(pageable));
    }

    // 특정 공연 조회
    @GetMapping("/{concertId}")
    public CommonResponse<GetConcertRes> getConcertById(@PathVariable("concertId") Long concertId) {
        return CommonResponse.success(concertService.getConcertById(concertId));
    }

    // 특정 공연 수정
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @PatchMapping("/{concertId}")
    public CommonResponse<GetConcertRes> updateConcertById(@PathVariable("concertId") Long concertId, @Valid @RequestBody UpdateConcertReq updateConcertReq) {
        concertService.updateConcertById(concertId, updateConcertReq);
        return CommonResponse.success(concertService.getConcertById(concertId));
    }

    // 특정 공연 삭제
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @DeleteMapping("/{concertId}")
    public CommonResponse deleteConcertById(@RequestHeader("X-Email") String email, @PathVariable("concertId") Long concertId) {
        concertService.deleteConcertById(concertId, email);
        return CommonResponse.success();
    }

    // Concert 정보 조회
    @GetMapping("/{concertId}/detail")
    public GetConcertRes getConcert(@PathVariable Long concertId) {
        return concertService.getConcert(concertId);
    }

    // 특정 판매자의 공연 조회
    @GetMapping("/{userId}/seller")
    public List<GetConcertDetailRes> getConcertBySeller(@PathVariable Long userId) {
        return concertService.getConcertBySeller(userId);
    }

    // 공연 이름으로 검색
    @GetMapping("/search")
    public CommonResponse<List<GetConcertDetailRes>> searchConcertByName(
            @RequestParam(required = false, name = "concert-name") String concertName
    ){
        return CommonResponse.success(concertService.searchConcertName(concertName));
    }

    // 특정 판매자의 공연중 해당 문자가 들어간 공연 조회
    @GetMapping("/{userId}/{concertName}/search")
    List<GetConcertDetailRes> searchConcert(@PathVariable Long userId, @PathVariable String concertName) {
        return concertService.searchConcert(userId, concertName);
    }

    // 해당 문자가 들어간 공연 조회
    @GetMapping("/{concertName}/search")
    List<GetConcertDetailRes> searchConcertName(@PathVariable String concertName) {
        return concertService.searchConcertName(concertName);
    }

}
