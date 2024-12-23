package com.fortickets.orderservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.common.util.GlobalUtil;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.context.BookingRollbackContext;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.request.DecrementScheduleReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.CreatePaymentRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.mapper.BookingMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProcessService {

    private final ConcertClient concertClient;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final RollbackService rollbackService;
    private final PaymentService paymentService;

    // 스케줄 좌석 감소
    public GetScheduleDetailRes decrementSeats(Integer quantity, Long scheduleId, BookingRollbackContext context) {
        try {
            GetScheduleDetailRes scheduleRes = concertClient.decrementSeats(new DecrementScheduleReq(quantity), scheduleId);
            context.addDecrementSchedule(quantity, scheduleId);
            return scheduleRes;
        } catch (FeignException e) {
            log.error("[Booking Service FeignException] decrementSeats error : {}", e.getMessage());
            throw new GlobalException(ErrorCase.SYSTEM_ERROR);
        }
    }

    // 예매 생성
    @Transactional
    public List<CreateBookingRes> createBooking(CreateBookingReq createBookingReq, GetScheduleDetailRes scheduleRes,
        BookingRollbackContext context) {
        try {
            log.info("createBookingReq : {}", createBookingReq);

            // 이미 예약된 좌석인지 확인
            List<Booking> bookings = new ArrayList<>();
            createBookingReq.seat().forEach(seat -> {
                // 좌석 형식 확인
                if (!GlobalUtil.isValidSeatFormat(seat)) {
                    throw new GlobalException(ErrorCase.INVALID_SEAT_FORMAT);
                }
                // 이미 예약된 좌석인지 확인
                bookingRepository.findByScheduleIdAndSeat(createBookingReq.scheduleId(), seat)
                    .ifPresent(booking -> {
                        throw new GlobalException(ErrorCase.ALREADY_BOOKED_SEAT);
                    });

                Booking booking = createBookingReq.toEntity(seat);
                booking.setConcertId(scheduleRes.concertId());
                bookings.add(booking);
            });

            // 예매 정보 저장
            bookingRepository.saveAll(bookings);

            // 롤백 정보 저장
            context.addCreateBooking(bookings.stream().map(Booking::getId).toList());

            return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();

        } catch (GlobalException e) {
            log.error("[Booking Service] createBooking error : {}", e.getMessage());
            rollbackService.decrementSeatsRollback(context);
            throw new GlobalException(e.getErrorCase());
        }
    }

    // 결제 생성
    public CreatePaymentRes createPayment(Long userId, List<CreateBookingRes> createBookingResList, BookingRollbackContext context) {
        try {
            List<Long> bookingIds = createBookingResList.stream().map(CreateBookingRes::id).toList();
            return paymentService.createPayment(new CreatePaymentReq(userId, bookingIds));
        } catch (GlobalException e) {
            log.error("[Booking Service] createPayment error : {}", e.getMessage());
            rollbackService.decrementSeatsRollback(context);
            rollbackService.createBookingRollback(context);
            throw new GlobalException(e.getErrorCase());
        }
    }
}
