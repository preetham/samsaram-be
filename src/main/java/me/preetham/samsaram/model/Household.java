package me.preetham.samsaram.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Entity
@Table(name = "household")
public class Household {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true)
  private Long id;
  private String name;
  private String imageUrl;
  private HouseholdState state;

  public Household() {
  }

  public Household(@NonNull String name, @NonNull String imageUrl) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.state = HouseholdState.ACTIVE;
  }
}
