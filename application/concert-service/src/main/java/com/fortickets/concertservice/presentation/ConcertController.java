package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {
  private final ConcertService concertService;

  // 공연생성 , ROLE : SELLER , MANAGER
  @PostMapping
  public CommonResponse<CreateConcertRes> createConcert(@RequestBody  CreateConcertReq createConcertReq){
    //todo userId 값 전달 필요
    Long userId = 1L;
    var createConcertRes = concertService.createConcert(createConcertReq,userId);
    return CommonResponse.success(createConcertRes);
  }
  @GetMapping
  public CommonResponse<Page<GetConcertRes>> getAllConcerts(Pageable pageable){
    return CommonResponse.success(concertService.getAllConcerts(pageable));
  }


}
