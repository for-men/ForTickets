package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.common.CommonResponse.CommonEmptyRes;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    public CommonResponse<CommonEmptyRes> createBook() {
        return null;
    }

    public CommonResponse<CreateBookingRes> createBooking(CreateBookingReq createBookingReq) {
        var createBookingRes = bookingService.createBooking(createBookingReq);
        return CommonResponse.success(createBookingRes);
    }
}
