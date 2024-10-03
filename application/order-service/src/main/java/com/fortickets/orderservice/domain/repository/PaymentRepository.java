package com.fortickets.orderservice.domain.repository;

import com.fortickets.orderservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
