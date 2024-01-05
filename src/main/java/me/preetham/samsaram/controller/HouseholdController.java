package me.preetham.samsaram.controller;

import me.preetham.samsaram.model.Household;
import me.preetham.samsaram.model.User;
import me.preetham.samsaram.repository.HouseholdRepository;
import me.preetham.samsaram.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/household", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class HouseholdController {
  @Autowired
  private HouseholdRepository householdRepository;

  @Autowired
  private IUserService userService;

  @GetMapping(path = "")
  @PreAuthorize("hasAuthority('SCOPE_samsaram-backend/read:household')")
  public @ResponseBody Household getAllHouseholds() {
    User user = userService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || user.getEmail() == null) {
      throw new BadJwtException("Unauthorized user");
    }
    return householdRepository.findHouseholdByOwner(user.getEmail());
  }
}
