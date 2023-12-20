package me.preetham.samsaram.model.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class HouseholdDTO {

  private final String name;
  private final String imageUrl;
  private final String state;

  public HouseholdDTO(@NonNull String name, @NonNull String imageUrl, @NonNull String state) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.state = state;
  }
}
