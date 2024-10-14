package com.fortickets.orderservice.domain.entity;

import static com.fortickets.jpa.BaseEntity.DELETED_FALSE;
import com.fortickets.common.PaymentStatus;
import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Table(name = "payment")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(DELETED_FALSE)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private Long userId;
    private Long concertId;
    private Long scheduleId;
    private Long totalPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String card;

    @OneToMany(mappedBy = "payment")
    private List<Booking> bookings;


    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public void waiting() {
        this.status = PaymentStatus.WAITING;
    }

    public void complete(String card) {
        this.status = PaymentStatus.COMPLETED;
        this.card = card;
    }
}
