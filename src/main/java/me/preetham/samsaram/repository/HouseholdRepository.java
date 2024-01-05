package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Household;
import org.springframework.data.repository.CrudRepository;

public interface HouseholdRepository extends CrudRepository<Household, Integer> {
  public Household findHouseholdByOwner(String owner);
}
