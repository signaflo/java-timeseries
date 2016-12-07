package data;

import org.junit.Test;

/**
 * Created by jacob on 12/7/16.
 */
public class ColumnSpec {

  @Test
  public void testColumn() {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    DataFrame df = reader.createDataFrame();
    Column<?> col = df.getColumn(0);
    Column<?> col2 = df.getColumn(1);
  }
}
