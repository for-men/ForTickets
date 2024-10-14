package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.entity.QConcert;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Concert> findByUserIdAndConcertNameContaining(Long userId, String concertName) {
        QConcert concert = QConcert.concert;

        return queryFactory.selectFrom(concert)
                .where(concert.userId.eq(userId)
                        // 공연 이름이 null일 경우 모든 공연을 조회
                        .and(concertName != null ? concert.concertName.containsIgnoreCase(concertName) : null))
                .fetch();
    }

    @Override
    public List<Concert> findByConcertNameContaining(String concertName) {
        QConcert concert = QConcert.concert;

        return queryFactory.selectFrom(concert)
                // 공연 이름이 null일 경우 모든 공연을 조회
                .where(concertName != null ? concert.concertName.containsIgnoreCase(concertName) : null)
                .fetch();
    }

}
