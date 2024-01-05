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
import lombok.Setter;
import me.preetham.samsaram.model.dto.TransactionDTO;

@Getter
@Entity
@Table(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true)
  private long id;
  private long date;
  private String description;
  private float amount;
  private int categoryId;
  @Setter
  private String userId;
  private String payee;
  private int bankId;
  @Enumerated(EnumType.STRING)
  private TransactionType type;
  private long accountNumber;

  public Transaction() {
  }

  public Transaction(TransactionDTO transactionDTO) {
    this.date = transactionDTO.getDate();
    this.description = transactionDTO.getDescription();
    this.amount = transactionDTO.getAmount();
    this.categoryId = transactionDTO.getCategoryId();
    this.payee = transactionDTO.getPayee();
    this.bankId = transactionDTO.getBankId();
    this.type = TransactionType.valueOf(transactionDTO.getTransactionType());
    this.accountNumber = transactionDTO.getAccountNumber();
  }

}
