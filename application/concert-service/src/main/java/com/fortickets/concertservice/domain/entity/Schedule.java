package com.fortickets.concertservice.domain.entity;

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
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule")
public class Schedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long scheduleId;

  // Concert와의 ManyToOne 관계 설정
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "concert_id")
  private Concert concert;

  // Stage와의 ManyToOne 관계 설정
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stage_id")
  private Stage stage;

  @Column(name = "concert_date", nullable = false)
  private LocalDate concertDate;

  @Column(name = "concert_time", nullable = false)
  private LocalTime concertTime;

}
