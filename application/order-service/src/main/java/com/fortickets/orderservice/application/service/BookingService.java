package com.fortickets.orderservice.application.service;

import com.fortickets.common.BookingStatus;
import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.ConfirmBookingReq;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.mapper.BookingMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserClient userClient;
    private final ConcertClient concertClient;
    private final PaymentService paymentService;

    @Transactional
    public List<CreateBookingRes> createBooking(Long userId, CreateBookingReq createBookingReq) {
        // TODO : 대기열
        if (!userId.equals(createBookingReq.userId())) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }

        // 이미 예약된 좌석인지 확인
        // 하나라도 예약된 좌석이 있으면 예외처리
        List<Booking> bookings = new ArrayList<>();
        createBookingReq.seat().forEach(seat -> {
            bookingRepository.findByScheduleIdAndSeat(createBookingReq.scheduleId(), seat)
                .ifPresent(booking -> {
                    throw new GlobalException(ErrorCase.ALREADY_BOOKED_SEAT);
                });
            Booking booking = createBookingReq.toEntity(seat);
            bookings.add(booking);
        });

        // 예매 정보 저장
        bookingRepository.saveAll(bookings);

        return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();
    }

    @Transactional
    public void confirmBooking(ConfirmBookingReq confirmBookingReq) {
        List<Booking> bookingList = bookingRepository.findAllById(confirmBookingReq.bookingIds());
        bookingList.forEach(Booking::confirm);

        CreatePaymentReq createPaymentReq = CreatePaymentReq.builder()
            .userId(bookingList.get(0).getUserId())
            .totalPrice(bookingList.stream().mapToLong(Booking::getPrice).sum())
            .concertId(bookingList.get(0).getConcertId())
            .scheduleId(bookingList.get(0).getScheduleId())
            .bookingIds(confirmBookingReq.bookingIds())
            .build();

        // 결제 생성
        paymentService.createPayment(createPaymentReq);
    }

    public Page<GetBookingRes> getBooking(String nickname, String concertName, Pageable pageable) {
        // TODO: role String에서 변경 필요
        List<GetUserRes> userList = new ArrayList<>();
        List<GetConcertRes> concertList = new ArrayList<>();
        // TODO: 검색 조건 null 체크 하려면 QueryDSL 필요
        // 닉네임으로 사용자 조회
        // 검색 조건에 없으면 조회할 필요 없음
        if (nickname != null) {
            userList = userClient.searchNickname(nickname);
        }
        if (concertName != null) {
            // 공연명으로 공연 조회
            concertList = concertClient.searchConcertName(concertName);
        }

        // 사용자, 공연명으로 예약 조회 null일 경우는 메서드에서 처리함
        Page<Booking> bookingList = bookingRepository.findByBookingSearch(
            userList.stream().map(GetUserRes::userId).toList(),
            concertList.stream().map(GetConcertRes::concertId).toList(), BookingStatus.PENDING, pageable);

        // GetConcertRes를 concertId 기준으로 찾기 위한 Map 생성
        Map<Long, GetConcertRes> concertMap = concertList.stream()
            .collect(Collectors.toMap(GetConcertRes::concertId, Function.identity()));


        // Booking의 concertId와 매칭되는 GetConcertRes를 찾아 매핑
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream()
            .map(booking -> {
                GetConcertRes concertRes = concertMap.get(booking.getConcertId());
                return bookingMapper.toGetBookingRes(booking, concertRes);
            }).toList();

        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public Page<GetBookingRes> getBookingBySeller(Long userId, Long sellerId, String role, String nickname, String concertName, Pageable pageable) {
        // 판매자와 요청자가 같은지 확인
        // TODO: role String에서 변경 필요
        if (!role.equals("MANAGER")) {
            if (!userId.equals(sellerId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        List<GetUserRes> userList = new ArrayList<>();
        List<GetConcertRes> concertList = new ArrayList<>();
        // TODO: 검색 조건 null 체크 하려면 QueryDSL 필요
        // 닉네임으로 사용자 조회
        // 검색 조건에 없으면 조회할 필요 없음
        if (nickname != null) {
            userList = userClient.searchNickname(nickname);
        }
        if (concertName != null) {
            // 공연명으로 공연 조회
           concertList = concertClient.searchConcert(sellerId, concertName);
        }else {
            concertList = concertClient.getConcertBySeller(sellerId);
        }

        // 사용자, 공연명으로 예약 조회 null일 경우는 메서드에서 처리함
        Page<Booking> bookingList = bookingRepository.findByBookingSearch(
            userList.stream().map(GetUserRes::userId).toList(),
            concertList.stream().map(GetConcertRes::concertId).toList(), BookingStatus.PENDING, pageable);

        // GetConcertRes를 concertId 기준으로 찾기 위한 Map 생성
        Map<Long, GetConcertRes> concertMap = concertList.stream()
            .collect(Collectors.toMap(GetConcertRes::concertId, Function.identity()));

        // Booking의 concertId와 매칭되는 GetConcertRes를 찾아 매핑
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream()
            .map(booking -> {
                GetConcertRes concertRes = concertMap.get(booking.getConcertId());
                return bookingMapper.toGetBookingRes(booking, concertRes);
            }).toList();

        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public Page<GetBookingRes> getBookingByUser(Long userId, Pageable pageable) {
        Page<Booking> bookingList = bookingRepository.findByUserId(userId, pageable);
        // TODO: 성능 개선 필요 (한번에 받아와서 처리 가능할 듯?)
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream().map(booking -> {
            GetConcertRes concertRes = concertClient.getConcert(booking.getConcertId());
            return bookingMapper.toGetBookingRes(booking, concertRes);
        }).toList();
        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public GetConcertDetailRes getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));
        GetScheduleDetailRes getScheduleDetailRes = concertClient.getScheduleDetail(booking.getScheduleId());

        return bookingMapper.toGetConcertDetailRes(booking, getScheduleDetailRes);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));
        // TODO: 예약 취소 가능한지 확인
        GetScheduleDetailRes scheduleRes = concertClient.getScheduleDetail(booking.getScheduleId());

        // 공연이 끝났는지 체크 필요
        if (!possibleCancel(scheduleRes.concertDate(), scheduleRes.concertTime())) {
            throw new GlobalException(ErrorCase.CANNOT_CANCEL_BOOKING);
        }

        // TODO: 결제 취소 요청
        // paymentService.cancelPayment(booking.getPaymentId());

        // 예약 취소
        booking.cancel();
    }

    @Transactional
    public void deleteBooking(String email, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));
        booking.delete(email);
    }

    private boolean possibleCancel(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime).isAfter(LocalDateTime.now());
    }

    public List<String> getSeatsByScheduleId(Long scheduleId) {
        BookingStatus pending = BookingStatus.PENDING;
        BookingStatus confirmed = BookingStatus.CONFIRMED;
        return bookingRepository.findSeatByScheduleId(scheduleId,pending,confirmed);
    }

}
