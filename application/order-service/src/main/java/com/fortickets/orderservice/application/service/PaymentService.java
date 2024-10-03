package com.fortickets.orderservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.entity.Payment;
import com.fortickets.orderservice.domain.mapper.PaymentMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import com.fortickets.orderservice.domain.repository.PaymentRepository;
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

    @Transactional
    public void createPayment(CreatePaymentReq createPaymentReq) {
        // 결제 생성
        Payment payment = paymentMapper.toEntity(createPaymentReq);
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
        if (nickname != null) {
            userResList = userClient.searchNickname(nickname);
        }

        Page<Payment> payments = paymentRepository.findByUserIdIn(userResList.stream().map(GetUserRes::userId).toList(), pageable);

        return payments.map(paymentMapper::toGetPaymentRes);
    }

    public Page<GetPaymentRes> getPaymentByUser(Long userId, Pageable pageable) {
        Page<Payment> paymentList = paymentRepository.findByUserId(userId, pageable);

        //
        List<GetPaymentRes> getPaymentResList = paymentList.getContent().stream().map( payment -> {
            var getConcertRes = concertClient.getConcert(payment.getConcertId());
            return paymentMapper.toGetPaymentUser(payment, getConcertRes);
        }).toList();

        return new PageImpl<>(getPaymentResList, pageable, paymentList.getTotalElements());
    }

    public GetPaymentDetailRes getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));
        return null;
    }
}
