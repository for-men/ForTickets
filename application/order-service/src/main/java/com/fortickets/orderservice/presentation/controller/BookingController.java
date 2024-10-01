package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.common.CommonResponse.CommonEmptyRes;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.service.BookingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 접근 권한 제한
    @PostMapping
    public CommonResponse<List<CreateBookingRes>> createBooking(CreateBookingReq createBookingReq) {
        var createBookingRes = bookingService.createBooking(createBookingReq);
        return CommonResponse.success(createBookingRes);
    }

    @GetMapping
    public CommonResponse<GetBookingRes> getBooking() {
        var getBookingRes = bookingService.getBooking();
        return CommonResponse.success();
    }
}
