package com.fortickets.concertservice.domain.entity;

import com.fortickets.jpa.BaseEntity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long concertId;

  @Column(nullable = false,length = 255)
  private String concertName;

  @Column(length = 255)
  private String concertImage;

  @Column(nullable = false)
  private int runtime;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date endDate;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private Long userId;

  @OneToMany(mappedBy = "concert",fetch = FetchType.LAZY)
  List<Schedule> schedules = new ArrayList<>();



  public static Concert of(Long userId, String concertName, int runtime, Date startDate, Date endDate, Long price, String concertImage) {
    return new Concert(userId,concertName,runtime,startDate,endDate,price,concertImage);
  }

  private Concert(Long userId, String concertName, int runtime, Date startDate, Date endDate, Long price, String concertImage) {
    this.userId = userId;
    this.concertName = concertName;
    this.runtime = runtime;
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
    this.concertImage = concertImage;
  }






}
