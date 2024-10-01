package com.fortickets.orderservice.domain.entity;

import com.fortickets.common.BookingStatus;
import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "booking")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    private Long paymentId;
    private Long scheduleId;
    private Long userId;
    private Long price;
    private BookingStatus status;
    private String seat;

    public void updateStatus(BookingStatus status) {
        this.status = status;
    }

    public Booking(Long scheduleId, Long userId, Long price, String seat) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.price = price;
        this.seat = seat;
        this.status = BookingStatus.PENDING;
    }
}
