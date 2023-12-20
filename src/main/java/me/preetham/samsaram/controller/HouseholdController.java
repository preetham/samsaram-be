package me.preetham.samsaram.controller;

import me.preetham.samsaram.model.Household;
import me.preetham.samsaram.model.dto.HouseholdDTO;
import me.preetham.samsaram.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/api/v1/household")
public class HouseholdController {
  @Autowired
  private HouseholdRepository householdRepository;

  @PostMapping(path = "")
  public @ResponseBody String addHousehold(@RequestBody HouseholdDTO householdDTO) {
    Household household = new Household(householdDTO.getName(), householdDTO.getImageUrl());
    householdRepository.save(household);
    return "Success";
  }

  @GetMapping(path = "")
  public @ResponseBody Iterable<Household> getAllHouseholds() {
    return householdRepository.findAll();
  }
}
