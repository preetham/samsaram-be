package me.preetham.samsaram.service;

import me.preetham.samsaram.model.User;
import org.springframework.security.core.Authentication;

public interface IUserService {
  public User getUserDetails(Authentication authentication);
}
