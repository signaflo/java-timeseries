package data;

import java.time.YearMonth;
import java.util.List;

import org.junit.Test;

public class CsvReaderSpec {
  
  @Test
  public void testFile() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    List<List<String>> rows = reader.parsedRecords();
    for (List<String> record : rows) {
      
      System.out.print(YearMonth.parse(record.get(0)).atDay(1));
      System.out.println(", " + Integer.parseInt(record.get(1)));
    }
  }
  
}
