package com.fortickets.concertservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id")
  private Long id;

  // Concert와의 ManyToOne 관계 설정
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonBackReference
  @JoinColumn(name = "concert_id")
  private Concert concert;

  // Stage와의 ManyToOne 관계 설정
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "stage_id")
  private Stage stage;

  @Column(name = "concert_date", nullable = false)
  private LocalDate concertDate;

  @Column(name = "concert_time", nullable = false)
  private LocalTime concertTime;


  public static Schedule of(Concert concert, Stage stage,LocalDate concertDate,LocalTime concertTime) {
    return new Schedule(concert,stage,concertDate,concertTime);
  }
  public Long getConcertId() {
    return concert.getId();  // concert의 ID만 가져오기
  }

  public Long getStageId() {
    return stage.getId();    // stage의 ID만 가져오기
  }

  private Schedule(Concert concert, Stage stage, LocalDate concertDate, LocalTime concertTime) {
    this.concert = concert;
    this.stage = stage;
    this.concertDate = concertDate;
    this.concertTime = concertTime;
  }

}
