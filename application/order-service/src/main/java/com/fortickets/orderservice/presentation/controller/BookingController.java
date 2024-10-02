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
@RequestMapping("/bookings")
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
     * 관리자 예매 내역 조회
     * @ROLE :MANAGER
     */
    @GetMapping
    public CommonResponse<Page<GetBookingRes>> getBooking(
        // TODO: default value qeurydsl 적용 후 삭제
        @RequestParam(required = false, name = "nickname", defaultValue = "sample") String nickname,
        @RequestParam(required = false, name = "concert-name", defaultValue = "sample") String concertName,
        Pageable pageable
    ) {
        return CommonResponse.success(bookingService.getBooking(nickname, concertName, pageable));
    }

    /**
     * 판매자 예매 내역 조회
     */
    @GetMapping("/seller/{sellerId}")
    public CommonResponse<Page<GetBookingRes>> getBookingBySeller(
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable Long sellerId,
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        return CommonResponse.success(bookingService.getBookingBySeller(userId, sellerId, role, nickname, concertName, pageable));
    }

    /**
     * 사용자 예매 내역 조회
     */
    @GetMapping("/me/{userId}")
    public CommonResponse<Page<GetBookingRes>> getBookingByUser(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        return CommonResponse.success(bookingService.getBookingByUser(userId, pageable));
    }

    /**
     * 예매 단건 조회
     */
    @GetMapping("/{bookingId}")
    public CommonResponse<GetBookingRes> getBookingById(
        @PathVariable Long bookingId
    ) {
        return CommonResponse.success(bookingService.getBookingById(bookingId));
    }
}
