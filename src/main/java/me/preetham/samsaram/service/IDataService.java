package me.preetham.samsaram.service;

import java.io.InputStream;
import java.util.List;
import me.preetham.samsaram.model.User;
import me.preetham.samsaram.model.dto.TransactionResponseDTO;

public interface IDataService {

  List<TransactionResponseDTO> extractTransactions(User user, InputStream fileInputStream, int bankId);
}
