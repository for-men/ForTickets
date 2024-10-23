package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, Long> {

}
