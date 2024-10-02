package com.fortickets.orderservice.presentation.controller;

import com.fortickets.orderservice.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
}
