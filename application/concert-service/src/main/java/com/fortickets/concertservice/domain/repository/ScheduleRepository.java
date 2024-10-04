package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
