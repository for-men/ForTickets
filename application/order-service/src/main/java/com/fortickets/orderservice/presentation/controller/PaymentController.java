package com.fortickets.orderservice.presentation.controller;

import com.fortickets.common.security.CustomUser;
import com.fortickets.common.security.UseAuth;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.request.RequestPaymentReq;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 생성 내부에서 예매 생성 시 결제 생성
     */
    @PostMapping
    public void createPayment(@Valid @RequestBody CreatePaymentReq createPaymentReq) {
        paymentService.createPayment(createPaymentReq);
    }

    /**
     * 결제 요청
     */
    @PatchMapping("/request")
    public CommonResponse<CommonEmptyRes> requestPayment(
        @UseAuth CustomUser customUser,
        @Valid @RequestBody RequestPaymentReq requestPaymentReq) {
        paymentService.requestPayment(customUser.getUserId(), requestPaymentReq);
        return CommonResponse.success();
    }

    /**
     * 결제 내역 전체 조회 (Manager)
     */
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping
    public CommonResponse<Page<GetPaymentRes>> getPayments(
        @RequestParam(required = false, name = "nickname") String nickname,
        Pageable pageable
    ) {
        return CommonResponse.success(paymentService.getPayments(nickname, pageable));
    }

    /**
     * 결제 내역 전체 조회 (Seller)
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'SELLER')")
    @GetMapping("/seller/{sellerId}")
    public CommonResponse<Page<GetPaymentRes>> getPaymentsBySeller(
        @UseAuth CustomUser customUser,
        @PathVariable Long sellerId,
        @RequestParam(required = false, name = "nickname") String nickname,
        Pageable pageable
    ) {
        return CommonResponse.success(paymentService.getPaymentsBySeller(customUser.getUserId(), customUser.getRole(), sellerId, nickname, pageable));
    }

    /**
     * 결제 내역 전체 조회 (User)
     */
    @GetMapping("/me/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public CommonResponse<Page<GetPaymentRes>> getBookingByUser(
        @UseAuth CustomUser customUser,
        @PathVariable Long userId, Pageable pageable) {
        return CommonResponse.success(paymentService.getPaymentByUser(customUser.getUserId(), customUser.getRole(), userId, pageable));
    }

    /**
     * 결제 단일 조회
     */
    @GetMapping("/{paymentId}")
    public CommonResponse<GetPaymentDetailRes> getPayment(
        @UseAuth CustomUser customUser,
        @PathVariable Long paymentId) {
        return CommonResponse.success(paymentService.getPayment(customUser.getUserId(), customUser.getRole(), paymentId));
    }

    /**
     * 결제 취소
     */
    @PatchMapping("/{paymentId}")
    public CommonResponse<CommonEmptyRes> cancelPayment(
        @UseAuth CustomUser customUser,
        @PathVariable Long paymentId) {
        paymentService.cancelPayment(customUser.getUserId(), customUser.getRole(), paymentId);
        return CommonResponse.success();
    }

    /**
     * 결제 내역 삭제
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{paymentId}")
    public CommonResponse<CommonEmptyRes> deletePayment(
        @UseAuth CustomUser customUser,
        @PathVariable Long paymentId) {
        paymentService.deletePayment(customUser.getEmail(), paymentId);
        return CommonResponse.success();
    }
}
