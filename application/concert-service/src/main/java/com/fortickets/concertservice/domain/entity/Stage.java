package com.fortickets.concertservice.domain.entity;

import com.fortickets.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stage extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stage_id")
  private Long id;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "location", length = 255, nullable = false)
  private String location;

  @Column(name = "row", nullable = false)
  private int row;

  @Column(name = "col", nullable = false)
  private int col;

  public static Stage of(String name, String location, int row, int col) {
    return new Stage(name, location, row, col);
  }

  private Stage(String name, String location, int row, int col) {
    this.name = name;
    this.location = location;
    this.row = row;
    this.col = col;
  }

}
