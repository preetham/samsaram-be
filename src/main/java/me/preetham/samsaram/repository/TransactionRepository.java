package me.preetham.samsaram.repository;

import java.util.Collection;
import java.util.List;
import me.preetham.samsaram.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
  public List<Transaction> findByUserId(String userId);
}
