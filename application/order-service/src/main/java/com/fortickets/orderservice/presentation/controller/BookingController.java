package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.common.CommonResponse.CommonEmptyRes;
import com.fortickets.orderservice.application.dto.request.ConfirmBookingReq;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.orderservice.application.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    public CommonResponse<List<CreateBookingRes>> createBooking(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody CreateBookingReq createBookingReq) {
        var createBookingRes = bookingService.createBooking(Long.valueOf(userId), createBookingReq);
        return CommonResponse.success(createBookingRes);
    }

    /**
     * 예매 확정
     */
    @PatchMapping("/confirm")
    public CommonResponse<CommonEmptyRes> confirmBooking(
        @Valid @RequestBody ConfirmBookingReq confirmBookingReq) {
        bookingService.confirmBooking(confirmBookingReq);
        return CommonResponse.success();
    }

    /**
     * 관리자 예매 내역 조회
     *
     * @ROLE :MANAGER
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public CommonResponse<Page<GetBookingRes>> getBooking(
        // TODO: default value qeurydsl 적용 후 삭제
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        return CommonResponse.success(bookingService.getBooking(nickname, concertName, pageable));
    }

    /**
     * 판매자 예매 내역 조회
     */
    @PreAuthorize("hasRole('SELLER') or hasRole('MANAGER')")
    @GetMapping("/seller/{sellerId}")
    public CommonResponse<Page<GetBookingRes>> getBookingBySeller(
        @PathVariable Long sellerId,
        @RequestHeader("X-Role") String role,
        @RequestHeader("X-User-Id") String userId,
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        // role, userid securitycontext에서 가져오기
        return CommonResponse.success(
            bookingService.getBookingBySeller(Long.valueOf(userId), sellerId, role, nickname, concertName, pageable));
    }

    /**
     * 사용자 예매 내역 조회
     */
    @GetMapping("/me/{userId}")
    public CommonResponse<Page<GetBookingRes>> getBookingByUser(@PathVariable Long userId, Pageable pageable) {
        return CommonResponse.success(bookingService.getBookingByUser(userId, pageable));
    }

    /**
     * 예매 단건 조회 (예매 상세 조회)
     */
    @GetMapping("/{bookingId}")
    public CommonResponse<GetConcertDetailRes> getBookingById(@PathVariable Long bookingId) {
        return CommonResponse.success(bookingService.getBookingById(bookingId));
    }

    /**
     * 예매 불가 좌석 조회
     */
    @GetMapping("/seats/{scheduleId}")
    public CommonResponse<List<String>> getSeatsByScheduleId(@PathVariable Long scheduleId) {
        return CommonResponse.success(bookingService.getSeatsByScheduleId(scheduleId));
    }

    /**
     * 예매 취소
     */
    @PatchMapping("/cancel/{bookingId}")
    public CommonResponse<CommonEmptyRes> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return CommonResponse.success();
    }

    /**
     * 예매 내역 삭제
     */
    @DeleteMapping("/{bookingId}")
    public CommonResponse<CommonEmptyRes> deleteBooking(
        @RequestHeader("X-Email") String email,
        @PathVariable Long bookingId) {
        bookingService.deleteBooking(email, bookingId);
        return CommonResponse.success();
    }
}
