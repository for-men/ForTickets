package com.fortickets.orderservice.domain.repository;

import com.fortickets.orderservice.domain.entity.Payment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByUserIdIn(List<Long> userIdList, Pageable pageable);

    Page<Payment> findByUserId(Long userId, Pageable pageable);
}
