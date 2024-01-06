package me.preetham.samsaram.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import me.preetham.samsaram.model.Transaction;
import me.preetham.samsaram.model.User;

public interface IDataService {

  List<Transaction> extractTransactions(User user, InputStream fileInputStream, int bankId);
}
