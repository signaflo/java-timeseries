package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CsvDataSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  private String csvPath;
  private CsvData csvData;

  @Before
  public void beforeMethod() {
    csvPath = "monthly-total-number-of-pigs-sla.csv";
    csvData = new CsvData(csvPath, true);
  }

  @Test
  public void whenNoColumnByThatNameThenExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    csvData.getColumn("doesn't exist");
  }

  @Test
  public void whenHeaderFalseThenHeaderIsEmptyList() {
    csvData = new CsvData(csvPath, false);
    assertThat(csvData.getHeader(), is(equalTo(Collections.emptyList())));
  }

  @Test
  public void whenConstructorNoHeaderThenHeaderIsEmptyList() {
    csvData = new CsvData(csvPath);
    assertThat(csvData.getHeader(), is(equalTo(Collections.emptyList())));
  }

  @Test
  public void whenCsvReadHeaderCorrect() throws Exception {
    assertThat("Pigs Slaughtered", is(equalTo(csvData.getHeader().get(1))));
  }

  @Test
  public void whenCsvReadNumRowsCorrect() throws Exception {
    assertThat(csvData.getRows().size(), is(equalTo(188)));
  }

  @Test
  public void whenCsvReadNumColsCorrect() throws Exception {
    assertThat(csvData.getColumns().size(), is(equalTo(2)));
  }

  @Test
  public void whenCsvRowReadDataCorrect() throws Exception {
    assertThat(Double.parseDouble(csvData.getRow(0).get(1)), is(equalTo(76378.0)));
  }

  @Test
  public void whenCsvColumnReadDataCorrect() throws Exception {
    assertThat(Double.parseDouble(csvData.getColumn(1).get(0)), is(equalTo(76378.0)));
  }

  @Test
  public void whenCsvFileReturnedTheExpectedFileIsSent() {
    assertThat(csvData.csvFile().getName(), is(equalTo(csvPath)));
  }

  @Test
  public void whenColumnByNameThenRightColumnDataReturned() {
    List<String> column = csvData.getColumn("Pigs Slaughtered");
    List<DataType> schema = new ArrayList<>(2);
    schema.add(DataType.STRING);
    schema.add(DataType.DOUBLE);
    DataFrame df = csvData.createDataFrame(schema);
    System.out.println(df.getColumnIds());
    df.replace("Month", Double[].class, (new Double[0]));
    Double[] months = df.get("Month", Double[].class);
    assertThat(Double.parseDouble(column.get(0)), is(equalTo(76378.0)));
  }

}
