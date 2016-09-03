package data;

import java.util.List;

import org.junit.Test;

public class CsvReaderSpec {
  
  @Test
  public void testFile() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    List<List<String>> rows = reader.getRows();
    double[] series = new double[rows.size()];
    int i = 0;
    for (List<String> record : rows) {
      //System.out.print(YearMonth.parse(record.get(0)).atDay(1));
      series[i++] = Double.parseDouble(record.get(1));
      
    }
    System.out.println(reader.getHeader().get(1));
    System.out.println(reader.getColumns().get(1));
  }
  
}
