package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.service.ConcertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {
  private final ConcertService concertService;

  // 공연생성 , ROLE : SELLER , MANAGER
  @PostMapping
  public CommonResponse<CreateConcertRes> createConcert(
      @RequestHeader("X-User-Id") String userId,
      @RequestBody  CreateConcertReq createConcertReq){
    var createConcertRes = concertService.createConcert(createConcertReq,Long.valueOf(userId));
    return CommonResponse.success(createConcertRes);
  }
  @GetMapping
  public CommonResponse<Page<GetConcertRes>> getAllConcerts(Pageable pageable){
    return CommonResponse.success(concertService.getAllConcerts(pageable));
  }

  // Concert 정보 조회
  @GetMapping("/{concertId}")
  public GetConcertRes getConcert(@PathVariable Long concertId){
    return concertService.getConcert(concertId);
  }

  @GetMapping("/{userId}/seller")
    public List<GetConcertDetailRes> getConcertBySeller(@PathVariable Long userId){
        return concertService.getConcertBySeller(userId);
    }

}
