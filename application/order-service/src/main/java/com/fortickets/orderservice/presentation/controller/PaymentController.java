package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.CommonResponse;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.service.PaymentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping
    public void createPayment(@RequestBody CreatePaymentReq createPaymentReq) {
        paymentService.createPayment(createPaymentReq);
    }

    /**
     * 결제 내역 전체 조회 (Manager)
     */
    @GetMapping
    public CommonResponse<Page<GetPaymentRes>> getPayments(
        @RequestParam(required = false, name = "nickname") String nickname,
        @RequestParam(required = false, name = "concert-name") String concertName,
        Pageable pageable
    ) {
        return CommonResponse.success(paymentService.getPayments(nickname, pageable));
    }

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
