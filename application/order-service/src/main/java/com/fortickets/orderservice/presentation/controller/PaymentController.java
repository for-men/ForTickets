package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable
    ) {
        return CommonResponse.success(paymentService.getPayments(nickname, pageable));
    }

    /**
     *  결제 내역 전체 조회 (Seller)
     */
    @GetMapping("/seller/{sellerId}")
    public CommonResponse<Page<GetPaymentRes>> getPaymentsBySeller(
        @RequestParam(required = false, name = "nickname") String nickname,
        Pageable pageable
    ) {
        return CommonResponse.success(paymentService.getPayments(nickname, pageable));
    }

    /**
     * 결제 내역 전체 조회 (User)
     */
    @GetMapping("/me/{userId}")
    public CommonResponse<Page<GetPaymentRes>> getBookingByUser(@PathVariable Long userId, Pageable pageable) {
        return CommonResponse.success(paymentService.getPaymentByUser(userId, pageable));
    }

    /**
     * 결제 단일 조회
     */
    @GetMapping("/{paymentId}")
    public CommonResponse<GetPaymentDetailRes> getPayment(@PathVariable Long paymentId) {
        return CommonResponse.success(paymentService.getPayment(paymentId));
    }

    /**
     *  결제 취소
     */
    @PatchMapping("/{paymentId}")
    public CommonResponse<CommonEmptyRes> cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
        return CommonResponse.success();
    }

    /**
     * 결제 내역 삭제
     */
    @DeleteMapping("/{paymentId}")
    public CommonResponse<CommonEmptyRes> deletePayment(
        @RequestHeader("X-Email") String email,
        @PathVariable Long paymentId) {
        // TODO: 이메일 정보 필요
        paymentService.deletePayment(email, paymentId);
        return CommonResponse.success();
    }
}
