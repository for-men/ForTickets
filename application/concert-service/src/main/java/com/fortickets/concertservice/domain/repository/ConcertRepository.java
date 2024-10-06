package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByUserId(Long userId);

    List<Concert> findByUserIdAndConcertNameContaining(Long userId, String concertName);

    List<Concert> findByConcertNameContaining(String concertName);
}
