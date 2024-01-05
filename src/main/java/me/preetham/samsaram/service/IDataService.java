package me.preetham.samsaram.service;

import java.io.File;
import java.util.List;
import me.preetham.samsaram.model.Transaction;

public interface IDataService {

  List<Transaction> extractTransactions(File file, int bankId);
}
