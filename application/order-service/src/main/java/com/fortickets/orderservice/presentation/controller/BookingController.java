package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성 본인만 가능
    @PostMapping
    public CommonResponse<List<CreateBookingRes>> createBooking(
        @UseAuth CustomUser customUser,
        @Valid @RequestBody CreateBookingReq createBookingReq) {
        var createBookingRes = bookingService.createBooking(customUser.getUserId(), createBookingReq);
        return CommonResponse.success(createBookingRes);
    }


    // 관리자 예매 내역 조회
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

    // 판매자 예매 내역 조회
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @GetMapping("/seller/{sellerId}")
    public CommonResponse<Page<GetBookingRes>> getBookingBySeller(
        @PathVariable Long sellerId,
        @UseAuth CustomUser customUser,
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        // role, userid securitycontext에서 가져오기
        return CommonResponse.success(
            bookingService.getBookingBySeller(sellerId, customUser.getUserId(), customUser.getRole(), nickname, concertName, pageable));
    }

    // 사용자 예매 내역 조회
    @GetMapping("/me/{userId}")
    public CommonResponse<Page<GetBookingRes>> getBookingByUser(
        @UseAuth CustomUser customUser,
        @PathVariable Long userId, Pageable pageable) {
        return CommonResponse.success(bookingService.getBookingByUser(customUser.getUserId(), customUser.getRole(), userId, pageable));
    }

    // 예매 단건 조회 (예매 상세 조회)
    @GetMapping("/{bookingId}")
    public CommonResponse<GetConcertDetailRes> getBookingById(
        @UseAuth CustomUser customUser,
        @PathVariable Long bookingId) {
        return CommonResponse.success(bookingService.getBookingById(customUser.getUserId(), customUser.getRole(), bookingId));
    }


    // 예매 취소
    @PatchMapping("/cancel/{bookingId}")
    public CommonResponse<CommonEmptyRes> cancelBooking(
        @UseAuth CustomUser customUser,
        @PathVariable Long bookingId) {
        bookingService.cancelBooking(customUser.getUserId(), customUser.getRole(), bookingId);
        return CommonResponse.success();
    }

    // 예매 내역 삭제
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{bookingId}")
    public CommonResponse<CommonEmptyRes> deleteBooking(
        @UseAuth CustomUser customUser,
        @PathVariable Long bookingId) {
        bookingService.deleteBooking(customUser.getEmail(), bookingId);
        return CommonResponse.success();
    }

    // 예매 불가 좌석 조회
    @GetMapping("/seats/{scheduleId}")
    public CommonResponse<List<String>> getSeatsByScheduleId(@PathVariable Long scheduleId) {
        return CommonResponse.success(bookingService.getSeatsByScheduleId(scheduleId));
    }
}
