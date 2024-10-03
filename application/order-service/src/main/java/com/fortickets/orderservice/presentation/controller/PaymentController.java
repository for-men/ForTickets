package com.fortickets.orderservice.presentation.controller;

import com.fortickets.orderservice.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     *  결제 생성
     *  내부에서 예매 생성 시 결제 생성
     */
    /**
     * 결제 내역 전체 조회 (Manager)
     */

    /**
     *  결제 내역 전체 조회 (Seller)
     */

    /**
     * 결제 내역 전체 조회 (User)
     */

    /**
     * 결제 단일 조회
     */

    /**
     *  결제 취소
     */

    /**
     * 결제 내역 삭제
     */
}
