package com.fortickets.orderservice.application.service;

import com.fortickets.common.GlobalUtil;
import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.request.RequestPaymentReq;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.entity.Payment;
import com.fortickets.orderservice.domain.mapper.PaymentMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import com.fortickets.orderservice.domain.repository.PaymentRepository;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final UserClient userClient;
    private final ConcertClient concertClient;

    /**
     * 결제 생성 (예매 확정 시 결제 생성)
     *
     * @param createPaymentReq
     */
    @Transactional
    public void createPayment(CreatePaymentReq createPaymentReq) {
        // 결제 생성
        Payment payment = paymentMapper.toEntity(createPaymentReq);
        payment.waiting();
        paymentRepository.save(payment);

        // 받아온 예매 아이디로 예매 조회
        List<Booking> bookingList = bookingRepository.findAllById(createPaymentReq.bookingIds());

        // 예매에 결제를 넣어줌
        bookingList.forEach(booking -> {
            booking.setPayment(payment);
        });

        // 예매 저장
        bookingRepository.saveAll(bookingList);
    }

    public Page<GetPaymentRes> getPayments(String nickname, Pageable pageable) {
        List<GetUserRes> userResList = new ArrayList<>();
        Page<Payment> payments = null;
        if (nickname != null) {
            userResList = userClient.searchNickname(nickname);
            payments = paymentRepository.findByUserIdIn(userResList.stream().map(GetUserRes::userId).toList(), pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }

        return payments.map(paymentMapper::toGetPaymentRes);
    }

    public Page<GetPaymentRes> getPaymentsBySeller(Long getUserId, String role, Long userId, String nickname, Pageable pageable) {
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(userId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        List<GetUserRes> userResList = new ArrayList<>();
        Page<Payment> payments = null;
        if (nickname != null) {
            userResList = userClient.searchNickname(nickname);
            payments = paymentRepository.findByUserIdIn(userResList.stream().map(GetUserRes::userId).toList(), pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }

        return payments.map(paymentMapper::toGetPaymentRes);
    }

    public Page<GetPaymentRes> getPaymentByUser(Long getUserId, String role, Long userId, Pageable pageable) {
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(userId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        Page<Payment> paymentList = paymentRepository.findByUserId(userId, pageable);

        List<GetPaymentRes> getPaymentResList = paymentList.getContent().stream().map(payment -> {
            var getConcertRes = concertClient.getConcert(payment.getConcertId());
            return paymentMapper.toGetPaymentUser(payment, getConcertRes);
        }).toList();

        return new PageImpl<>(getPaymentResList, pageable, paymentList.getTotalElements());
    }

    public GetPaymentDetailRes getPayment(Long getUserId, String role, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(payment.getUserId())) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }

        var getScheduleRes = concertClient.getScheduleDetail(payment.getScheduleId());
        var getUserRes = userClient.getUser(payment.getUserId());

        return paymentMapper.toGetPaymentDetailRes(payment, getScheduleRes, getUserRes);
    }

    @Transactional
    public void cancelPayment(Long getUserId, String role, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        // TODO : 결제 취소
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(payment.getUserId())) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        payment.cancel();

        // 예매 취소
        List<Booking> bookingList = bookingRepository.findByPaymentId(paymentId);
        bookingList.forEach(Booking::cancel);
    }

    @Transactional
    public void deletePayment(String email, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        payment.delete(email);
    }

    @Transactional
    public void requestPayment(Long getUserId, RequestPaymentReq requestPaymentReq) {
        // 결제 요청 -> 결제 완료
        Payment payment = paymentRepository.findById(requestPaymentReq.paymentId())
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        if (!getUserId.equals(payment.getUserId())) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }

        try {
            payment.complete(GlobalUtil.hash(requestPaymentReq.card()));
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException(ErrorCase.INVALID_INPUT);
        }

        // 결제 완료 시 예매 확정
        List<Booking> bookingList = bookingRepository.findByPaymentId(requestPaymentReq.paymentId());
        bookingList.forEach(Booking::confirm);

    }
    //                                          카드번호 받기
    // 좌석 선택 (예매 생성) -> 결제 요청(결제 ID를 예매에 넣어주고 결제 생성) -> 결제 완료 시 예매 확정
    // 예매 생성 -> 결제 요청 (응답 결제 정보) -> (토스가쏴주는)결제완료 API (req 결제 ID , 예매 확정)
}
