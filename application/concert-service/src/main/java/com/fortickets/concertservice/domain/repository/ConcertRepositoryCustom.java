package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import java.util.List;

public interface ConcertRepositoryCustom {

    List<Concert> findByUserIdAndConcertNameContaining(Long userId, String concertName);

    List<Concert> findByConcertNameContaining(String concertName);

}
