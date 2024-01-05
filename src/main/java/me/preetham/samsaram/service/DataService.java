package me.preetham.samsaram.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.preetham.samsaram.model.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataService implements IDataService {
  Logger logger = LoggerFactory.getLogger(DataService.class);

  @Override
  public List<Transaction> extractTransactions(File file, int bankId) {
    try (Workbook workbook = new XSSFWorkbook(file)) {
      int numberOfSheets = workbook.getNumberOfSheets();
      if (numberOfSheets < 1) {
        logger.error("No sheets in workbook");
        return new ArrayList<>();
      }
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rowIterator = sheet.rowIterator();
      List<Transaction> transactions = new ArrayList<>();
      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        Transaction transaction = null;
        while (cellIterator.hasNext()) {
          Cell cell = cellIterator.next();
          switch (cell.getCellType()) {
            case STRING:
              break;
            case NUMERIC:
              break;
            default:
              continue;
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return new ArrayList<>();
  }
}
