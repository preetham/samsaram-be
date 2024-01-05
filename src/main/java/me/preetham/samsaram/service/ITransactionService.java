package me.preetham.samsaram.service;

import java.util.List;
import me.preetham.samsaram.model.Transaction;
import me.preetham.samsaram.model.dto.TransactionDTO;

public interface ITransactionService {
  List<Transaction> getAllTransactionsForUser(String userId);
  List<Transaction> getAllHouseholdTransactions(String userId);
  void addTransaction(String userId, TransactionDTO transactionDTO);
  void addMultipleTransaction(String userId, List<TransactionDTO> transactionDTOList);
}
