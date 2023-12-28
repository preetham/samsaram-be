package me.preetham.samsaram.controller;

import me.preetham.samsaram.model.Bank;
import me.preetham.samsaram.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/bank", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {
  @Autowired
  private BankRepository bankRepository;

  @GetMapping(path = "")
  @PreAuthorize("hasAuthority('SCOPE_samsaram-backend/read:household')")
  public @ResponseBody Iterable<Bank> getAllBanks() {
    return bankRepository.findAll();
  }
}
