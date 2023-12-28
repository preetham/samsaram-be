package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Bank;
import org.springframework.data.repository.CrudRepository;

public interface BankRepository extends CrudRepository<Bank, Long> {
}
