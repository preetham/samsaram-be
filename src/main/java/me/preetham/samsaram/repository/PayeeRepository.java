package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Payee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PayeeRepository extends ElasticsearchRepository<Payee, String> {
  Payee findPayeeByName(String name);
}
