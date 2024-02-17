package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Payee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

public interface PayeeRepository extends ElasticsearchRepository<Payee, String> {

  @Query("{\"query\":{\"query_string\":{ \"query\":\"?0\"}}}")
  Payee findPayeeByName(String name);
}
