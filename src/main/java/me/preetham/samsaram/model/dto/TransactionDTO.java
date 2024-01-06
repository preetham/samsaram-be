package me.preetham.samsaram.model.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class TransactionDTO {

  private final long date;
  private final String description;
  private final double amount;
  private final int categoryId;
  private final String payee;
  private final int bankId;
  private final String transactionType;
  private final long accountNumber;

  public TransactionDTO(long date, @NonNull String description, double amount, int categoryId,
      String payee,
      int bankId, @NonNull String transactionType, long accountNumber) {
    this.date = date;
    this.description = description;
    this.amount = amount;
    this.categoryId = categoryId;
    this.payee = payee;
    this.bankId = bankId;
    this.transactionType = transactionType;
    this.accountNumber = accountNumber;
  }
}
