package com.fortickets.orderservice.domain.entity;

import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "booking")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    private Long payment_id;
    private Long concert_id;
    private Long user_id;
    private Long price;
    private BookingStatus status;
    private String seat;
}
