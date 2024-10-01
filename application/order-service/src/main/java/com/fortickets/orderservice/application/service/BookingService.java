package com.fortickets.orderservice.application.service;

import com.fortickets.common.ErrorCase;
import com.fortickets.exception.GlobalException;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.mapper.BookingMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        // TODO: 로그인한 사용자와 요청한 사용자가 같은지 확인
        // TODO: 관리자인 경우

        // TODO: 존재하는 스케줄인지 확인
        var schedule = concertClient.getSchedule(createBookingReq.scheduleId());
        if (schedule == null) {
            throw new GlobalException(ErrorCase.NOT_EXIST_SCHEDULE);
        }

        // 이미 예약된 좌석인지 확인
        // 하나라도 예약된 좌석이 있으면 예외처리
        List<Booking> bookings = new ArrayList<>();
        createBookingReq.seat().forEach(seat -> {
            bookingRepository.findByScheduleIdAndSeat(createBookingReq.scheduleId(), seat)
                .ifPresent(booking -> {
                    throw new GlobalException(ErrorCase.ALREADY_BOOKED_SEAT);
                });
            var booking = new Booking(createBookingReq.scheduleId(), createBookingReq.userId(), createBookingReq.price(), seat);
            bookings.add(booking);
        });

        // 예매 정보 저장
        bookingRepository.saveAll(bookings);

        return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();
    }


    public GetBookingRes getBooking() {
        return null;
    }
}
