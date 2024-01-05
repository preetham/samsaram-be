package me.preetham.samsaram.service;

import java.io.File;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataService implements IDataService {
  Logger logger = LoggerFactory.getLogger(DataService.class);

  @Override
  public void extractTransactions(File file, int bankId) {
    try {
      Workbook workbook = new XSSFWorkbook(file);
    } catch (Exception e) {

    }

  }
}
