package me.preetham.samsaram.controller;

import java.util.ArrayList;
import java.util.List;
import me.preetham.samsaram.model.Transaction;
import me.preetham.samsaram.model.User;
import me.preetham.samsaram.service.IDataService;
import me.preetham.samsaram.service.ITransactionService;
import me.preetham.samsaram.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/v1/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class TransactionController {

  Logger logger = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private IUserService userService;

  @Autowired
  private ITransactionService transactionService;

  @Autowired
  private IDataService dataService;

  @GetMapping(path = "")
  @PreAuthorize("hasAuthority('SCOPE_samsaram-backend/read:household')")
  public @ResponseBody List<Transaction> getAllTransactionsForUser() {
    User user = userService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || user.getEmail() == null) {
      throw new BadJwtException("Unauthorized user");
    }
    return transactionService.getAllTransactionsForUser(user.getEmail());
  }

  @GetMapping(path = "/household")
  @PreAuthorize("hasAuthority('SCOPE_samsaram-backend/read:household')")
  public @ResponseBody List<Transaction> getAllHouseholdTransactions() {
    User user = userService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
    if (user == null || user.getEmail() == null) {
      logger.error("Unauthorised user: " + user);
      throw new BadJwtException("Unauthorized user");
    }
    return transactionService.getAllHouseholdTransactions(user.getEmail());
  }

  @PostMapping(path = "/upload")
  @PreAuthorize("hasAuthority('SCOPE_samsaram-backend/read:household')")
  public @ResponseBody List<Transaction> uploadTransactionData(@RequestParam("file") MultipartFile file,
      @RequestParam("bank_id") int bankId) {
//    User user = userService.getUserDetails(SecurityContextHolder.getContext().getAuthentication());
//    if (user == null || user.getEmail() == null) {
//      logger.error("Unauthorised user: " + user);
//      throw new BadJwtException("Unauthorized user");
//    }
    User user = new User("example@gmail.com", "Preetham");
    try {
      return dataService.extractTransactions(user, file.getInputStream(), bankId);
    } catch (Exception e) {
      logger.error("Error while extracting file data: " + e.getMessage());
      throw new IllegalArgumentException();
    }
  }
}
