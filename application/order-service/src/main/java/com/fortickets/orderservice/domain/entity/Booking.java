package com.fortickets.orderservice.domain.entity;

import static com.fortickets.common.jpa.BaseEntity.DELETED_FALSE;
import com.fortickets.common.jpa.BaseEntity;
import com.fortickets.common.util.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Table(name = "booking")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(DELETED_FALSE)
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Setter
    private Long concertId;
    private Long scheduleId;
    private Long userId;
    private Long price;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private String seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Booking(Long scheduleId, Long userId, Long price, String seat) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.price = price;
        this.seat = seat;
        this.status = BookingStatus.PENDING;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELED;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void requestCancel() {
        this.status = BookingStatus.CANCEL_REQUESTED;
    }

}
