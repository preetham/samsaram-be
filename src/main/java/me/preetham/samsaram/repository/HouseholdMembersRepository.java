package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.HouseholdMembers;
import org.springframework.data.repository.CrudRepository;

public interface HouseholdMembersRepository extends CrudRepository<HouseholdMembers, Integer> {
  public Iterable<HouseholdMembers> findByHouseholdId(int householdId);
}
