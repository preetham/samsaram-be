package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
  public Transaction findByUserId(String userId);
}
