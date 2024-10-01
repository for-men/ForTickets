package com.fortickets.orderservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.res.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.mapper.BookingMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Transactional
    public List<CreateBookingRes> createBooking(CreateBookingReq createBookingReq) {
        // TODO : 대기열
        // TODO: 로그인한 사용자와 요청한 사용자가 같은지 확인
        // TODO: 관리자인 경우

        // TODO: 존재하는 스케줄인지 확인
        var schedule = concertClient.getSchedule(createBookingReq.scheduleId());

        // 이미 예약된 좌석인지 확인
        // 하나라도 예약된 좌석이 있으면 예외처리
        List<Booking> bookings = new ArrayList<>();
        createBookingReq.seat().forEach(seat -> {
            bookingRepository.findByScheduleIdAndSeat(createBookingReq.scheduleId(), seat)
                .ifPresent(booking -> {
                    throw new GlobalException(ErrorCase.ALREADY_BOOKED_SEAT);
                });
            var booking = createBookingReq.toEntity(seat);
            bookings.add(booking);
        });

        // 예매 정보 저장
        bookingRepository.saveAll(bookings);

        return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();
    }


    public Page<GetBookingRes> getBooking(Long userId, Long requestId, String role, String nickname, String concertName, Pageable pageable) {
        // TODO: role String에서 변경 필요
        if (!role.equals("MANAGER")) {
            if (!userId.equals(requestId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        // TODO: 검색 조건 null 체크 하려면 QueryDSL 필요
        // 닉네임으로 사용자 조회
        GetUserRes user = userClient.searchNickname(nickname);
        // 공연명으로 공연 조회
        GetConcertRes concert = concertClient.searchConcertName(concertName);

        Page<Booking> bookingList = bookingRepository.findByUserIdAndConcertId(user.userId(), concert.concertId(), pageable);
        return bookingList.map(bookingMapper::toGetBookingRes);
    }

    public Page<GetBookingRes> getBookingBySeller(Long userId, Long sellerId, String nickname, String concertName, Pageable pageable) {
        // 판매자와 요청자가 같은지 확인
        if (!userId.equals(sellerId)) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }

        // 닉네임으로 사용자 조회
        GetUserRes user = userClient.searchNickname(nickname);
        // 공연명으로 공연 조회
        GetConcertRes concert = concertClient.searchConcertName(concertName);

        return null;
    }
}

