package me.preetham.samsaram.service;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.preetham.samsaram.model.Bank;
import me.preetham.samsaram.model.Transaction;
import me.preetham.samsaram.model.TransactionProcessState;
import me.preetham.samsaram.model.TransactionType;
import me.preetham.samsaram.model.User;
import me.preetham.samsaram.repository.BankRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService implements IDataService {

  @Autowired
  private BankRepository bankRepository;

  private static final Pattern accountNumberPattern = Pattern.compile("\\d{12,15}");
  private static final Pattern amountPattern = Pattern.compile("^(\\d+(?:[\\.\\,]\\d{0,2})?)$");
  Logger logger = LoggerFactory.getLogger(DataService.class);

  @Override
  public List<Transaction> extractTransactions(User user, InputStream fileInputStream, int bankId) {
    try (Workbook workbook = new HSSFWorkbook(fileInputStream)) {
      int numberOfSheets = workbook.getNumberOfSheets();
      if (numberOfSheets < 1) {
        logger.error("No sheets in workbook");
        return new ArrayList<>();
      }
      Bank bank = bankRepository.findById(bankId).get();
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rowIterator = sheet.rowIterator();
      List<Transaction> transactions = new ArrayList<>();
      TransactionProcessState transactionProcessState = TransactionProcessState.not_found;
      long accountNumber = 0;
      while (rowIterator.hasNext() && transactionProcessState != TransactionProcessState.done) {
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        Transaction transaction = new Transaction();
        transaction.setBankId(bankId);
        transaction.setUserId(user.getEmail());
        while (cellIterator.hasNext() && transactionProcessState != TransactionProcessState.done) {
          Cell cell = cellIterator.next();
          switch (cell.getCellType()) {
            case STRING:
              if (transactionProcessState == TransactionProcessState.not_found && accountNumber <= 0) {
                accountNumber = parseAccountNumber(cell);
              }
              if (checkTransactionStart(cell, bank)) {
                transactionProcessState = TransactionProcessState.processing;
                continue;
              }
              if (transactionProcessState == TransactionProcessState.processing) {
                transaction = parseDate(cell, bank, transaction);
                transaction = parseDescription(cell, bank, transaction);
                transaction = parseAmount(cell, bank, transaction);
              }
              break;
            case BLANK:
              if (transactionProcessState == TransactionProcessState.processing) {
                transactionProcessState = TransactionProcessState.done;
              }
            default:
          }
        }
        if (transaction.getDescription() == null) {
          continue;
        }
        transaction.setAccountNumber(accountNumber);
        transactions.add(transaction);
      }
      return transactions;
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return new ArrayList<>();
  }

  private Transaction parseAmount(Cell cell, Bank bank, Transaction transaction) {
    if (cell.getColumnIndex() != bank.getCreditColumn()
        && cell.getColumnIndex() != bank.getDebitColumn()) {
      return transaction;
    }
    String amtStr = cell.getStringCellValue().trim();
    if (!amountPattern.matcher(amtStr).matches()) {
      return transaction;
    }
    double amount = Double.parseDouble(amtStr);
    if (amount <= 0) {
      return transaction;
    }
    transaction.setAmount(amount);
    if (cell.getColumnIndex() == bank.getDebitColumn()) {
      transaction.setType(TransactionType.debit);
    }
    if (cell.getColumnIndex() == bank.getCreditColumn()) {
      transaction.setType(TransactionType.credit);
    }
    return transaction;
  }

  private Transaction parseDate(Cell cell, Bank bank, Transaction transaction)
      throws ParseException {
    if (cell.getColumnIndex() != bank.getDateColumn()) {
      return transaction;
    }
    String txnDateStr = cell.getStringCellValue().trim();
    Pattern datePattern = Pattern.compile(bank.getDatePattern());
    if (!datePattern.matcher(txnDateStr).matches()) {
      return transaction;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat(bank.getDateFormat());
    Date date = dateFormat.parse(txnDateStr);
    long unixFormat = date.getTime() / 1000;
    transaction.setDate(unixFormat);
    return transaction;
  }

  private Transaction parseDescription(Cell cell, Bank bank, Transaction transaction) {
    if (cell.getColumnIndex() != bank.getDescriptionColumn()) {
      return transaction;
    }
    transaction.setDescription(cell.getStringCellValue().trim());
    transaction.setPayee("");
    transaction.setAccountNumber(0);
    transaction.setCategoryId(27);
    return transaction;
  }

  private boolean checkTransactionStart(Cell cell, Bank bank) {
    String cellValue = cell.getStringCellValue().trim();
    return cellValue.equalsIgnoreCase(bank.getHeaderColumn());
  }

  private long parseAccountNumber(Cell cell) {
    String cellValue = cell.getStringCellValue().trim();
    Matcher accMatcher = accountNumberPattern.matcher(cellValue);
    if (!accMatcher.find()) {
      return 0;
    }
    return Long.parseLong(accMatcher.group());
  }
}
