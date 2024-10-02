package com.fortickets.concertservice.domain.entity;

import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stage extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stage_id")
  private Long stageId;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "location", length = 255, nullable = false)
  private String location;

  @Column(name = "row", nullable = false)
  private Integer row;

  @Column(name = "col", nullable = false)
  private Integer col;

}
