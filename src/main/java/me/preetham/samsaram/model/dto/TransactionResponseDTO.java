package me.preetham.samsaram.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.preetham.samsaram.model.Payee;

@Getter
@Setter
public class TransactionResponseDTO {

  private long id;
  private long date;
  private String description;
  private double amount;
  private int categoryId;
  private String userId;
  private Payee payee;
  private int bankId;
  private String type;
  private long accountNumber;
}
