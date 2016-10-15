package data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CsvReaderSpec {
  
  @Test
  public void whenHeaderFalseThenHeaderIsEmptyList() {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, false);
    assertThat(reader.getHeader(), is(equalTo(Collections.emptyList())));
    
  }
  
  @Test
  public void whenCsvReadHeaderCorrect() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat("Pigs Slaughtered", is(equalTo(reader.getHeader().get(1))));
  }
  
  @Test
  public void whenCsvReadNumRowsCorrect() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat(reader.getRows().size(), is(equalTo(188)));
  }
  
  @Test
  public void whenCsvReadNumColsCorrect() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat(reader.getColumns().size(), is(equalTo(2)));
  }
  
  @Test
  public void whenCsvRowReadDataCorrect() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat(Double.parseDouble(reader.getRow(0).get(1)), is(equalTo(76378.0)));
  }
  
  @Test
  public void whenCsvColumnReadDataCorrect() throws Exception {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat(Double.parseDouble(reader.getColumn(1).get(0)), is(equalTo(76378.0)));
  }
  
  @Test
  public void whenCsvFileReturnedTheExpectedFileIsSent() {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    assertThat(reader.csvFile().getName(), is(equalTo(csvPath)));
  }
  
  @Test
  public void whenColumnByNameThenRightColumnDataReturnd() {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    List<String> column = reader.getColumn("Pigs Slaughtered");
    assertThat(Double.parseDouble(column.get(0)), is(equalTo(76378.0)));
  }
  
}
