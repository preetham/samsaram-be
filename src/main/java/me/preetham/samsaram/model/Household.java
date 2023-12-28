package me.preetham.samsaram.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
  private int id;
  private String name;
  private String imageUrl;
  @Enumerated(EnumType.STRING)
  private EntityState status;
  private String owner;

  public Household() {
  }

  public Household(@NonNull String name, @NonNull String imageUrl, String owner) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.status = EntityState.active;
    this.owner = owner;
  }
}
