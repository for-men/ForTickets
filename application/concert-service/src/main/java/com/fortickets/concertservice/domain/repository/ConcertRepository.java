package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertRepositoryCustom {

    List<Concert> findByUserId(Long userId);

}
