package com.fortickets.orderservice.domain.repository;

import com.fortickets.orderservice.domain.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
