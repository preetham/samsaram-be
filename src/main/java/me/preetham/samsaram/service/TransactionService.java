package me.preetham.samsaram.service;

import java.util.ArrayList;
import java.util.List;
import me.preetham.samsaram.model.HouseholdMembers;
import me.preetham.samsaram.model.Transaction;
import me.preetham.samsaram.model.dto.TransactionDTO;
import me.preetham.samsaram.repository.HouseholdMembersRepository;
import me.preetham.samsaram.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements ITransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private HouseholdMembersRepository householdMembersRepository;

  @Override
  public List<Transaction> getAllTransactionsForUser(String userId) {
    return transactionRepository.findByUserId(userId);
  }

  @Override
  public List<Transaction> getAllHouseholdTransactions(String userId) {
    HouseholdMembers householdMember = householdMembersRepository.findHouseholdMembersByUserId(
        userId);
    Iterable<HouseholdMembers> householdMembers = householdMembersRepository.findByHouseholdId(
        householdMember.getHouseholdId());
    List<Transaction> transactions = new ArrayList<>();
    householdMembers.forEach(member -> {
      transactions.addAll(transactionRepository.findByUserId(member.getUserId()));
    });
    return transactions;
  }

  @Override
  public void addTransaction(String userId, TransactionDTO transactionDTO) {
    Transaction transaction = new Transaction(transactionDTO);
    transaction.setUserId(userId);
    transactionRepository.save(transaction);
  }

  @Override
  public void addMultipleTransaction(String userId, List<TransactionDTO> transactionDTOList) {
    List<Transaction> transactions = transactionDTOList.stream().map(transactionDTO -> {
      Transaction transaction = new Transaction(transactionDTO);
      transaction.setUserId(userId);
      return transaction;
    }).toList();
    transactionRepository.saveAll(transactions);
  }
}
