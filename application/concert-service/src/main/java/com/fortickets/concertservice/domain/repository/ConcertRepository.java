package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConcertRepository extends JpaRepository<Concert, Long> {

}
