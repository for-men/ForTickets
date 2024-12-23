package com.fortickets.orderservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.BookingStatus;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.orderservice.application.client.ConcertClient;
import com.fortickets.orderservice.application.client.UserClient;
import com.fortickets.orderservice.application.context.BookingRollbackContext;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingAndPaymentRes;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.CreatePaymentRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.mapper.BookingMapper;
import com.fortickets.orderservice.domain.repository.BookingRepository;
import com.fortickets.orderservice.infrastructure.messaging.KafkaProducer;
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

    private final KafkaProducer kafkaProducer;

    private final ProcessService processService;

    // 예매 생성 본인만 가능
//    @Transactional
//    public List<CreateBookingRes> createBookings(Long userId, CreateBookingReq createBookingReq) {
//
//        log.info("createBookingReq : {}", createBookingReq);
//
//        // 중복 좌석 예약 확인
//        if (createBookingReq.seat().size() >= 2) {
//            Set<String> set = new HashSet<>();
//            if (createBookingReq.seat().stream().anyMatch(e -> !set.add(e))) {
//                throw new GlobalException(ErrorCase.DUPLICATE_SEAT);
//            }
//        }
//        // 분산 락 이름 정의 (예약 스케줄 ID 기반)
//        String lockKey = "bookingLock:" + createBookingReq.scheduleId();
//        RLock lock = redissonClient.getLock(lockKey); // 락 객체 생성
//
//        // 락을 획득하고, 예외 발생 시 락 해제
//        try {
//            lock.lock(); // 락 획득
//
//            // 요청 사용자와 예약 사용자가 같은지 확인
//            if (!userId.equals(createBookingReq.userId())) {
//                throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
//            }
//
//            // 스케줄 정보 조회
//            GetScheduleDetailRes scheduleRes = concertClient.getScheduleDetail(createBookingReq.scheduleId());
//
//            // 이미 예약된 좌석인지 확인
//            List<Booking> bookings = new ArrayList<>();
//            createBookingReq.seat().forEach(seat -> {
//                // 좌석 형식 확인
//                if (!GlobalUtil.isValidSeatFormat(seat)) {
//                    throw new GlobalException(ErrorCase.INVALID_SEAT_FORMAT);
//                }
//                // 이미 예약된 좌석인지 확인
//                bookingRepository.findByScheduleIdAndSeat(createBookingReq.scheduleId(), seat)
//                    .ifPresent(booking -> {
//                        throw new GlobalException(ErrorCase.ALREADY_BOOKED_SEAT);
//                    });
//
//                Booking booking = createBookingReq.toEntity(seat);
//                booking.setConcertId(scheduleRes.concertId());
//                bookings.add(booking);
//            });
//
//            // 예매 정보 저장
//            bookingRepository.saveAll(bookings);
//
//            return bookings.stream().map(bookingMapper::toCreateBookingRes).toList();
//
//        } finally {
//            // 락 해제
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
//    }

    // 예매 생성 본인만 가능 (보상 트랜잭션 적용)
    @Transactional
    public CreateBookingAndPaymentRes createBookingAndPayment(Long userId, CreateBookingReq createBookingReq) {

        log.info("createBookingReq : {}", createBookingReq);

        // 1. 중복 좌석 예약 확인
        if (createBookingReq.seat().size() >= 2) {
            Set<String> set = new HashSet<>();
            if (createBookingReq.seat().stream().anyMatch(e -> !set.add(e))) {
                throw new GlobalException(ErrorCase.DUPLICATE_SEAT);
            }
        }

        // 요청 사용자와 예약 사용자가 같은지 확인
        if (!userId.equals(createBookingReq.userId())) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }

        // 분산 락 이름 정의 (예약 스케줄 ID 기반)
        String lockKey = "bookingLock:" + createBookingReq.scheduleId();
        RLock lock = redissonClient.getLock(lockKey); // 락 객체 생성

        BookingRollbackContext rollbackContext = new BookingRollbackContext();
        // 락을 획득하고, 예외 발생 시 락 해제
        try {
            lock.lock(); // 락 획득

            // 2. 좌석 개수 차감
            GetScheduleDetailRes scheduleRes = processService.decrementSeats(createBookingReq.seat().size(),
                createBookingReq.scheduleId(), rollbackContext);

            // 3. 예매 생성
            List<CreateBookingRes> bookingResList = processService.createBooking(createBookingReq, scheduleRes, rollbackContext);

            // 4. 결제 생성
            CreatePaymentRes paymentRes = processService.createPayment(createBookingReq.userId(), bookingResList, rollbackContext);

            return new CreateBookingAndPaymentRes(bookingResList, paymentRes);
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 관리자 예매 내역 조회
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

    // 판매자 예매 내역 조회
    public Page<GetBookingRes> getBookingBySeller(Long sellerId, Long userId, String role, String nickname, String concertName,
        Pageable pageable) {
        // 판매자와 요청자가 같은지 확인
        validateAuthorization(role, userId, sellerId);
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

    // 사용자 예매 내역 조회
    public Page<GetBookingRes> getBookingByUser(Long getUserId, String role, Long userId, Pageable pageable) {
        // 요청 사용자와 예약 사용자가 같은지 확인
        validateAuthorization(role, getUserId, userId);
        Page<Booking> bookingList = bookingRepository.findByUserId(userId, pageable);

        List<GetBookingRes> getBookingResList = bookingList.getContent().stream().map(booking -> {
            GetConcertRes concertRes = concertClient.getConcert(booking.getConcertId());
            return bookingMapper.toGetBookingRes(booking, concertRes);
        }).toList();
        return new PageImpl<>(getBookingResList, pageable, bookingList.getTotalElements());
    }

    // 예매 단건 조회 (예매 상세 조회)
    public GetConcertDetailRes getBookingById(Long getUserId, String role, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));

        validateAuthorization(role, getUserId, booking.getUserId());

        GetScheduleDetailRes getScheduleDetailRes = concertClient.getScheduleDetail(booking.getScheduleId());

        return bookingMapper.toGetConcertDetailRes(booking, getScheduleDetailRes);
    }

    // 예매 취소
    @Transactional
    public void cancelBooking(Long getUserId, String role, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));

        validateAuthorization(role, getUserId, booking.getUserId());

        GetScheduleDetailRes scheduleRes = concertClient.getScheduleDetail(booking.getScheduleId());

        // 공연이 끝났는지 체크 필요
        if (!possibleCancel(scheduleRes.concertDate(), scheduleRes.concertTime())) {
            throw new GlobalException(ErrorCase.CANNOT_CANCEL_BOOKING);
        }

        // 취소 요청 됨(중간 상태) 상태로(사용자가 본인이 요청 중이라는 것을 알 수 있도록) 변경
        booking.requestCancel();

        // Kafka에 예매 취소 메시지 전송
        String message = "Booking canceled for ID: " + bookingId;
        log.info("1. 예매 취소를 위한 Kafka 메시지 전송 준비 중입니다. Booking ID: {}", bookingId);

        kafkaProducer.sendMessage("booking-cancel-topic", message);

        log.info("4. 예매 취소를 위한 Kafka 메시지가 전송되었습니다. Booking ID: {}", bookingId);
    }

    // 예매 내역 삭제
    @Transactional
    public void deleteBooking(String email, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new GlobalException(ErrorCase.BOOKING_NOT_FOUND));
        booking.delete(email);
    }

    // 예매 불가 좌석 조회
    public List<String> getSeatsByScheduleId(Long scheduleId) {
        BookingStatus pending = BookingStatus.PENDING;
        BookingStatus confirmed = BookingStatus.CONFIRMED;
        return bookingRepository.findSeatByScheduleId(scheduleId, pending, confirmed);
    }

    private boolean possibleCancel(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime).isAfter(LocalDateTime.now());
    }

    private void validateAuthorization(String role, Long userId, Long targetUserId) {
        if (!role.equals("ROLE_MANAGER") && !userId.equals(targetUserId)) {
            throw new GlobalException(ErrorCase.NOT_AUTHORIZED);
        }
    }

}
