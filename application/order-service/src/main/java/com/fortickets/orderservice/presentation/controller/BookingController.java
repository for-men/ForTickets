package com.fortickets.orderservice.presentation.controller;

import com.fortickets.orderservice.application.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

}
