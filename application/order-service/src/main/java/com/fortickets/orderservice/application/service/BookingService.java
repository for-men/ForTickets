package com.fortickets.orderservice.application.service;

import com.fortickets.common.util.BookingStatus;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.GlobalUtil;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserClient userClient;

    private final ConcertClient concertClient;

    private final PaymentService paymentService;

    private final RedissonClient redissonClient;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<CreateBookingRes> createBooking(Long userId, CreateBookingReq createBookingReq) {

        log.info("createBookingReq : {}", createBookingReq);

        // 중복 좌석 예약 확인
        if (createBookingReq.seat().size() >= 2) {
            Set<String> set = new HashSet<>();
            if (createBookingReq.seat().stream().anyMatch(e -> !set.add(e))) {
                throw new GlobalException(ErrorCase.DUPLICATE_SEAT);
            }
        }
        // 분산 락 이름 정의 (예약 스케줄 ID 기반)
        String lockKey = "bookingLock:" + createBookingReq.scheduleId();
        RLock lock = redissonClient.getLock(lockKey); // 락 객체 생성

        // 락을 획득하고, 예외 발생 시 락 해제
        try {
            lock.lock(); // 락 획득

            // 요청 사용자와 예약 사용자가 같은지 확인
            if (!userId.equals(createBookingReq.userId())) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }

            // 스케줄 정보 조회
            GetScheduleDetailRes scheduleRes = concertClient.getScheduleDetail(createBookingReq.scheduleId());

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

            return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();

        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void confirmBooking(Long getUserId, ConfirmBookingReq confirmBookingReq) {
        // 요청 사용자와 예약 사용자가 같은지 확인
        if (!getUserId.equals(confirmBookingReq.userId())) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }
        // 예매 정보 조회
        List<Booking> bookingList = bookingRepository.findAllByIdInAndStatusAndUserId(
            confirmBookingReq.bookingIds(), BookingStatus.PENDING,
            confirmBookingReq.userId());

        // 예매 정보가 없으면 예외 발생
        if (bookingList.isEmpty()) {
            throw new GlobalException(ErrorCase.BOOKING_NOT_FOUND);
        }
//        // 예매 정보 상태 변경 CONFIRMED
//        bookingList.forEach(Booking::confirm);

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
        List<GetUserRes> userList = new ArrayList<>();
        List<GetConcertRes> concertList = new ArrayList<>();
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
//        Page<Booking> bookingList = bookingRepository.findByBookingSearch(
//                userList.stream().map(GetUserRes::userId).toList(),
//                concertList.stream().map(GetConcertRes::id).toList(), BookingStatus.PENDING, pageable);

        List<Long> userIds = userList.stream().map(GetUserRes::userId).toList();
        List<Long> concertIds = concertList.stream().map(GetConcertRes::id).toList();

        // QueryDSL 사용
        Page<Booking> bookingList = bookingRepository.findByBookingSearch(userIds, concertIds, pageable);

        // GetConcertRes를 concertId 기준으로 찾기 위한 Map 생성
        Map<Long, GetConcertRes> concertMap = concertList.stream()
            .collect(Collectors.toMap(GetConcertRes::id, Function.identity()));

        // Booking의 concertId와 매칭되는 GetConcertRes를 찾아 매핑
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream()
            .map(booking -> {
                GetConcertRes concertRes = concertMap.get(booking.getConcertId());
                return bookingMapper.toGetBookingRes(booking, concertRes);
            }).toList();

        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public Page<GetBookingRes> getBookingBySeller(Long userId, Long sellerId, String role, String nickname, String concertName,
        Pageable pageable) {
        // 판매자와 요청자가 같은지 확인
        if (!role.equals("ROLE_MANAGER")) {
            if (!userId.equals(sellerId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        List<GetUserRes> userList = new ArrayList<>();
        List<GetConcertRes> concertList = new ArrayList<>();
        // 닉네임으로 사용자 조회
        // 검색 조건에 없으면 조회할 필요 없음
        if (nickname != null) {
            userList = userClient.searchNickname(nickname);
        }
        if (concertName != null) {
            // 공연명으로 공연 조회
            concertList = concertClient.searchConcert(sellerId, concertName);
        } else {
            concertList = concertClient.getConcertBySeller(sellerId);
        }

        // 사용자, 공연명으로 예약 조회 null일 경우는 메서드에서 처리함
//        Page<Booking> bookingList = bookingRepository.findByBookingSearch(
//            userList.stream().map(GetUserRes::userId).toList(),
//            concertList.stream().map(GetConcertRes::id).toList(), BookingStatus.PENDING, pageable);

        List<Long> userIds = userList.stream().map(GetUserRes::userId).toList();
        List<Long> concertIds = concertList.stream().map(GetConcertRes::id).toList();

        // QueryDSL 사용
        Page<Booking> bookingList = bookingRepository.findByBookingSearch(userIds, concertIds, pageable);

        // GetConcertRes를 concertId 기준으로 찾기 위한 Map 생성
        Map<Long, GetConcertRes> concertMap = concertList.stream()
            .collect(Collectors.toMap(GetConcertRes::id, Function.identity()));

        // Booking의 concertId와 매칭되는 GetConcertRes를 찾아 매핑
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream()
            .map(booking -> {
                GetConcertRes concertRes = concertMap.get(booking.getConcertId());
                return bookingMapper.toGetBookingRes(booking, concertRes);
            }).toList();

        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public Page<GetBookingRes> getBookingByUser(Long getUserId, String role, Long userId, Pageable pageable) {
        // 요청 사용자와 예약 사용자가 같은지 확인
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(userId)) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        Page<Booking> bookingList = bookingRepository.findByUserId(userId, pageable);
        // TODO: 성능 개선 필요 (한번에 받아와서 처리 가능할 듯?)
        List<GetBookingRes> getBookingResList = bookingList.getContent().stream().map(booking -> {
            GetConcertRes concertRes = concertClient.getConcert(booking.getConcertId());
            return bookingMapper.toGetBookingRes(booking, concertRes);
        }).toList();
        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    public GetConcertDetailRes getBookingById(Long getUserId, String role, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));
        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(booking.getUserId())) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        GetScheduleDetailRes getScheduleDetailRes = concertClient.getScheduleDetail(booking.getScheduleId());

        return bookingMapper.toGetConcertDetailRes(booking, getScheduleDetailRes);
    }

    @Transactional
    public void cancelBooking(Long getUserId, String role, Long bookingId) {

        // TODO : 이거 좀 애매함 확인 필요 -> 결제 취소
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));

        if (!role.equals("ROLE_MANAGER")) {
            if (!getUserId.equals(booking.getUserId())) {
                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
            }
        }
        GetScheduleDetailRes scheduleRes = concertClient.getScheduleDetail(booking.getScheduleId());

        // 공연이 끝났는지 체크 필요
        if (!possibleCancel(scheduleRes.concertDate(), scheduleRes.concertTime())) {
            throw new GlobalException(ErrorCase.CANNOT_CANCEL_BOOKING);
        }

        paymentService.cancelPayment(getUserId, role, booking.getPayment().getId());
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
        return bookingRepository.findSeatByScheduleId(scheduleId, pending, confirmed);
    }

}
