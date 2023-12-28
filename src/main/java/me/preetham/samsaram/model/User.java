package me.preetham.samsaram.model;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class User {
  private final String email;
  private final String name;

  public User(@NonNull String email, @NonNull String name) {
    this.email = email;
    this.name = name;
  }
}
