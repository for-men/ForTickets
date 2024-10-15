package com.fortickets.concertservice.domain.entity;

import static com.fortickets.common.jpa.BaseEntity.DELETED_FALSE;

import com.fortickets.common.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "stage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(DELETED_FALSE)
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

  //수정 메서드
  public void changeName(String name){
    this.name = name;
  }
  public void changeLocation(String location){
    this.location = location;
  }
  public void changeRow(int row){
    this.row = row;
  }
  public void changeCol(int col){
    this.col = col;
  }


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
