package me.preetham.samsaram.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;

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
  private String userId;
  private String payee;
  private int bankId;
  private TransactionType type;
  private long accountNumber;

  public Transaction() {
  }

  public Transaction(long date, @NonNull String description, float amount, int categoryId,
      String userId, @NonNull String payee, int bankId, TransactionType type, long accountNumber) {
    this.date = date;
    this.description = description;
    this.amount = amount;
    this.categoryId = categoryId;
    this.userId = userId;
    this.payee = payee;
    this.bankId = bankId;
    this.type = type;
    this.accountNumber = accountNumber;
  }
}
