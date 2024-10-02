package com.fortickets.concertservice.domain;

import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.sql.Date;
import java.util.ArrayList;
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

  @OneToMany(mappedBy = "concert",fetch = FetchType.LAZY)
  List<Schedule> schedules = new ArrayList<>();

//  todo 유저 엔티티와 연관관계 설정 필요
//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "user_id")
//  private User user;






}
