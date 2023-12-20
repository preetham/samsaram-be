package me.preetham.samsaram.service;

import me.preetham.samsaram.model.Household;
import me.preetham.samsaram.model.dto.HouseholdDTO;

public interface HouseholdService {
  long createHousehold(HouseholdDTO householdDTO);
  Household readHousehold(long householdId);
  Household updateHousehold(long householdId, HouseholdDTO householdDTO);
  void deleteHousehold(long householdId);
}
