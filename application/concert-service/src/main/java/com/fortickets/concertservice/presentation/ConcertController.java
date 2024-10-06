package com.fortickets.concertservice.presentation;

import com.fortickets.common.CommonResponse;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.request.UpdateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
import com.fortickets.concertservice.application.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public CommonResponse<Page<GetConcertsRes>> getAllConcerts(Pageable pageable){
    return CommonResponse.success(concertService.getAllConcerts(pageable));
  }
  @GetMapping("/{concertId}")
  public CommonResponse<GetConcertRes> getConcertById(@PathVariable("concertId") Long concertId){
    return CommonResponse.success(concertService.getConcertById(concertId));
  }
  @PatchMapping ("/{concertId}")
  public CommonResponse<GetConcertRes> updateConcertById(@PathVariable("concertId") Long concertId, @RequestBody UpdateConcertReq updateConcertReq){
    concertService.updateConcertById(concertId,updateConcertReq);
    return CommonResponse.success(concertService.getConcertById(concertId));
  }
  @DeleteMapping("/{concertId}")
  public CommonResponse deleteConcertById(@PathVariable("concertId") Long concertId){
    // todo email 값 불러오기 필요
    String email = "user@user.io";
    concertService.deleteConcertById(concertId, email);
    return CommonResponse.success();
  }


}
