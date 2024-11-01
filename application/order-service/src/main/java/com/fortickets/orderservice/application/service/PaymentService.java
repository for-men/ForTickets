package com.fortickets.orderservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.common.util.GlobalUtil;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.request.RequestPaymentReq;
import com.fortickets.orderservice.application.dto.response.CreatePaymentRes;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.entity.Payment;
import com.fortickets.orderservice.domain.mapper.PaymentMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import com.fortickets.orderservice.domain.repository.PaymentRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    // 결제 생성 (예매 확정 시 결제 생성)
    @Transactional
    public CreatePaymentRes createPayment(CreatePaymentReq createPaymentReq) {
        // 결제 생성
        Payment payment = paymentMapper.toEntity(createPaymentReq);
        // 결제 대기 상태로 세팅
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

        return paymentMapper.toCreatePaymentRes(payment);
    }

    // 결제 내역 전체 조회 (Manager)
    public Page<GetPaymentRes> getPayments(String nickname, Pageable pageable) {
        // 주어진 닉네임에 따른 사용자 ID 리스트
        List<Long> userIds = getUserIdsByNickname(nickname);
        // 사용자 ID 리스트를 기준으로 결제 조회
        Page<Payment> payments = findPaymentsByUserIds(userIds, pageable);

        // 콘서트 ID 리스트를 생성
        List<Long> concertIds = payments.getContent().stream()
            .map(Payment::getConcertId)
            .distinct() // 중복된 콘서트 ID 제거
            .toList();

        // 콘서트 정보를 일괄 조회
        Map<Long, GetConcertRes> concertMap = fetchConcertsByIds(concertIds);

        // 결제를 GetPaymentRes 객체로 매핑
        List<GetPaymentRes> getPaymentResList = mapPaymentsToResponse(payments, concertMap);

        return new PageImpl<>(getPaymentResList, pageable, payments.getTotalElements());
    }

    // 결제 내역 전체 조회 (Seller)
    public Page<GetPaymentRes> getPaymentsBySeller(Long getUserId, String role, Long userId, String nickname, Pageable pageable) {
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(userId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        // 주어진 닉네임에 따른 사용자 ID 리스트
        List<Long> userIds = getUserIdsByNickname(nickname);
        // 사용자 ID 리스트를 기준으로 결제 조회
        Page<Payment> payments = findPaymentsByUserIds(userIds, pageable);

        // 콘서트 ID 리스트를 생성
        List<Long> concertIds = payments.getContent().stream()
            .map(Payment::getConcertId)
            .distinct() // 중복된 콘서트 ID 제거
            .toList();

        // 콘서트 정보를 일괄 조회
        Map<Long, GetConcertRes> concertMap = fetchConcertsByIds(concertIds);

        // 결제를 GetPaymentRes 객체로 매핑
        List<GetPaymentRes> getPaymentResList = mapPaymentsToResponse(payments, concertMap);

        return new PageImpl<>(getPaymentResList, pageable, payments.getTotalElements());
    }

    // 결제 내역 전체 조회 (User)
    public Page<GetPaymentRes> getPaymentByUser(Long getUserId, String role, Long userId, Pageable pageable) {
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(userId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);

        // 콘서트 ID 리스트를 생성
        List<Long> concertIds = payments.getContent().stream()
            .map(Payment::getConcertId)
            .distinct() // 중복된 콘서트 ID 제거
            .toList();

        // 콘서트 정보를 일괄 조회
        Map<Long, GetConcertRes> concertMap = fetchConcertsByIds(concertIds);

        // 결제를 GetPaymentRes 객체로 매핑
        List<GetPaymentRes> getPaymentResList = mapPaymentsToResponse(payments, concertMap);

        return new PageImpl<>(getPaymentResList, pageable, payments.getTotalElements());
    }

    // 결제 단일 조회
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

    // 결제 취소
    @Transactional
    public void cancelPayment(Long getUserId, String role, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

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

    // 결제 내역 삭제
    @Transactional
    public void deletePayment(String email, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        payment.delete(email);
    }

    // 결제 요청
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

    @Transactional
    public void cancelPaymentTest(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new GlobalException(ErrorCase.NOT_FOUND_PAYMENT));

        payment.cancel();

        // 예매 취소
        List<Booking> bookingList = bookingRepository.findByPaymentId(paymentId);
        bookingList.forEach(booking -> {
            booking.cancel();
            bookingRepository.save(booking);
        });
    }


    /**
     * 주어진 닉네임으로 사용자 ID 리스트 가져오기. 닉네임이 null인 경우 빈 리스트를 반환
     */
    private List<Long> getUserIdsByNickname(String nickname) {
        if (nickname == null) {
            return Collections.emptyList(); // 닉네임이 null일 경우 빈 리스트 반환
        }
        // 닉네임으로 사용자 정보를 검색하여 사용자 ID 리스트를 반환합니다.
        List<GetUserRes> userResList = userClient.searchNickname(nickname);
        return userResList.stream().map(GetUserRes::userId).toList();
    }

    private Page<Payment> findPaymentsByUserIds(List<Long> userIds, Pageable pageable) {
        if (userIds == null || userIds.isEmpty()) {
            // userIds가 null이거나 비어있으면 모든 결제를 반환합니다.
            return paymentRepository.findAll(pageable);
        }
        // userIds가 존재하는 경우, 해당 사용자 ID로 결제를 반환합니다.
        return paymentRepository.findByUserIdIn(userIds, pageable);
    }

    /**
     * 주어진 콘서트 ID 리스트로 콘서트 정보를 일괄 조회하여 Map으로 반환
     */
    private Map<Long, GetConcertRes> fetchConcertsByIds(List<Long> concertIds) {
        // 콘서트 ID가 없으면 빈 맵 반환
        if (concertIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 모든 콘서트 정보를 한 번에 조회합니다.
        return concertClient.getConcertsByIds(concertIds).stream()
            .collect(Collectors.toMap(GetConcertRes::id, Function.identity()));
    }

    /**
     * 결제 리스트를 GetPaymentRes 객체로 매핑
     */
    private List<GetPaymentRes> mapPaymentsToResponse(Page<Payment> payments, Map<Long, GetConcertRes> concertMap) {
        return payments.getContent().stream()
            .map(payment -> {
                // 미리 조회한 콘서트 정보를 맵에서 가져오기
                GetConcertRes getConcertRes = concertMap.get(payment.getConcertId());
                return paymentMapper.toGetPaymentUser(payment, getConcertRes);
            })
            .toList();
    }
}
