package com.fortickets.orderservice.domain.repository;


import com.fortickets.common.util.BookingStatus;
import com.fortickets.orderservice.domain.entity.Booking;
import com.fortickets.orderservice.domain.entity.QBooking;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Booking> findByBookingSearch(List<Long> userIds, List<Long> concertIds, Pageable pageable) {
        // Booking Entity에 대응되는 QBooking 객체를 통해 매핑하여 필드에 접근
        QBooking booking = QBooking.booking;

        // BooleanExpression : QueryDSL에서 조건을 표현하는 객체, where()절에 사용된다.
        // userIds 리스트가 존재할 경우 booking.userId가 userIds에 포함된 데이터만 필터링
        BooleanExpression userCondition = userIds != null && !userIds.isEmpty() ? booking.userId.in(userIds) : null;
        // concertIds 리스트가 존재할 경우 booking.concertId가 concertIds에 포함된 데이터만 필터링
        BooleanExpression concertCondition = concertIds != null && !concertIds.isEmpty() ? booking.concertId.in(concertIds) : null;

        // 전체 예약을 조회할 조건
        boolean isUserConditionEmpty = userCondition == null;
        boolean isConcertConditionEmpty = concertCondition == null;

        List<Booking> results;

        if (isUserConditionEmpty && isConcertConditionEmpty) {
            // nickname과 concertName이 모두 비어있을 때: 전체 예약 조회
            results = queryFactory.selectFrom(booking)
                    .where(booking.status.ne(BookingStatus.PENDING)) // BookingStatus가 PENDING은 제외
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (isUserConditionEmpty) {
            // nickname만 비어있고 concertName이 있을 때: concertName에 해당하는 조건만 조회
            results = queryFactory.selectFrom(booking)
                    .where(concertCondition, booking.status.ne(BookingStatus.PENDING)) // BookingStatus가 PENDING은 제외
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (isConcertConditionEmpty) {
            // concertName만 비어있고 nickname이 있을 때: nickname에 해당하는 조건만 조회
            results = queryFactory.selectFrom(booking)
                    .where(userCondition, booking.status.ne(BookingStatus.PENDING)) // BookingStatus가 PENDING은 제외
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else {
            // nickname과 concertName 둘 다 있을 때: 두 조건에 해당하는 예약 조회
            results = queryFactory.selectFrom(booking)
                    .where(userCondition, concertCondition, booking.status.ne(BookingStatus.PENDING)) // BookingStatus가 PENDING은 제외
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        // 쿼리의 총 결과 수
        long total = queryFactory.selectFrom(booking)
                .where(userCondition, concertCondition, booking.status.ne(BookingStatus.PENDING))
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<String> findSeatByScheduleId(Long scheduleId, BookingStatus pending, BookingStatus confirmed) {
        QBooking booking = QBooking.booking;

        // 상태 조건을 설정
        BooleanExpression statusCondition = booking.status.eq(BookingStatus.PENDING)
                .or(booking.status.eq(BookingStatus.CONFIRMED));

        return queryFactory
                .select(booking.seat)
                .from(booking)
                .where(
                        booking.scheduleId.eq(scheduleId), // 주어진 scheduleId에 해당하는 예약
                        statusCondition // PENDING 또는 CONFIRMED 상태인 경우
                )
                .fetch();
    }

}
