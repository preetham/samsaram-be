package me.preetham.samsaram.service;

import java.io.File;

public interface IDataService {

  void extractTransactions(File file, int bankId);
}
