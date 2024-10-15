package com.fortickets.concertservice.domain.entity;

import static com.fortickets.common.jpa.BaseEntity.DELETED_FALSE;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fortickets.common.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "concert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(DELETED_FALSE)
public class Concert extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "concert_id")
  private Long Id;

  @Column(nullable = false,length = 255)
  private String concertName;

  @Column(length = 255)
  private String concertImage;

  @Column(nullable = false)
  private int runtime;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private LocalDate endDate;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private Long userId;

  @OneToMany(mappedBy = "concert",fetch = FetchType.LAZY)
  @JsonManagedReference
  List<Schedule> schedules = new ArrayList<>();

  // 수정 method
  public void changeImage(String s) {
    this.concertImage = s;
  }
  public void changeRuntime(int i) {
    this.runtime = i;
  }
  public void changeStartDate(LocalDate d) {
    this.startDate = d;
  }
  public void changeEndDate(LocalDate d) {
    this.endDate = d;
  }
  public void changePrice(Long price) {
    this.price = price;
  }
  public void changeConcertName(String concertName) {
    this.concertName = concertName;
  }
  public void changeName(String s) {
    this.concertName = s;
  }

  public static Concert of(Long userId, String concertName, int runtime, LocalDate startDate, LocalDate endDate, Long price, String concertImage) {
    return new Concert(userId,concertName,runtime,startDate,endDate,price,concertImage);
  }

  private Concert(Long userId, String concertName, int runtime, LocalDate startDate, LocalDate endDate, Long price, String concertImage) {
    this.userId = userId;
    this.concertName = concertName;
    this.runtime = runtime;
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
    this.concertImage = concertImage;
  }



}
