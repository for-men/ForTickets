package com.fortickets.orderservice.domain.entity;

import com.fortickets.common.BookingStatus;
import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private Long concertId;
    private Long scheduleId;
    private Long userId;
    private Long price;
    private BookingStatus status;
    private String seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    public void updateStatus(BookingStatus status) {
        this.status = status;
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Booking(Long scheduleId, Long concertId, Long userId, Long price, String seat) {
        this.scheduleId = scheduleId;
        this.concertId = concertId;
        this.userId = userId;
        this.price = price;
        this.seat = seat;
        this.status = BookingStatus.PENDING;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELED;
    }
}
