package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.service.BookingService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // TODO : 인가 처리

    /**
     * 예매 생성
     */
    @PostMapping
    public CommonResponse<List<CreateBookingRes>> createBooking(@RequestBody CreateBookingReq createBookingReq) {
        var createBookingRes = bookingService.createBooking(createBookingReq);
        return CommonResponse.success(createBookingRes);
    }

    /**
     * 예매 내역 조회 (관리자용)
     * 모든 예매 조회 가능
     */
    @GetMapping
    public CommonResponse<Page<GetBookingRes>> getBooking(
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        // 헤더 값 가져와야 됨
        var getBookingRes = bookingService.getBooking(nickname, concertName, pageable);
        return CommonResponse.success(getBookingRes);
    }

    @GetMapping("/seller")
    public CommonResponse<Page<GetBookingRes>> getBookingBySeller(
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        var getBookingRes = bookingService.getBookingBySeller(nickname, concertName, pageable);
        return CommonResponse.success(getBookingRes);
    }
}
