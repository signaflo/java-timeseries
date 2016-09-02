package data;

import java.time.YearMonth;

import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

public class CsvReaderSpec {
  
  @Test
  public void testFile() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader<YearMonth, Integer> reader = new CsvReader<>(csvPath, YearMonth.class, Integer.class);
    for (CSVRecord record : reader.parser()) {
      System.out.print(YearMonth.parse(record.get(0)).atDay(1));
      System.out.println(", " + Integer.parseInt(record.get(1)));
    }
    reader.parser().close();
  }
}
