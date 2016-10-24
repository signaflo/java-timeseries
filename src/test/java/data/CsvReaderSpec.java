package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CsvReaderSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  private String csvPath;
  private CsvReader reader;

  @Before
  public void beforeMethod() {
    csvPath = "monthly-total-number-of-pigs-sla.csv";
    reader = new CsvReader(csvPath, true);
  }

  @Test
  public void whenNoColumnByThatNameThenExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    reader.getColumn("doesn't exist");
  }

  @Test
  public void whenHeaderFalseThenHeaderIsEmptyList() {
    reader = new CsvReader(csvPath, false);
    assertThat(reader.getHeader(), is(equalTo(Collections.emptyList())));
  }

  @Test
  public void whenConstructorNoHeaderThenHeaderIsEmptyList() {
    reader = new CsvReader(csvPath);
    assertThat(reader.getHeader(), is(equalTo(Collections.emptyList())));
  }

  @Test
  public void whenCsvReadHeaderCorrect() throws Exception {
    assertThat("Pigs Slaughtered", is(equalTo(reader.getHeader().get(1))));
  }

  @Test
  public void whenCsvReadNumRowsCorrect() throws Exception {
    assertThat(reader.getRows().size(), is(equalTo(188)));
  }

  @Test
  public void whenCsvReadNumColsCorrect() throws Exception {
    assertThat(reader.getColumns().size(), is(equalTo(2)));
  }

  @Test
  public void whenCsvRowReadDataCorrect() throws Exception {
    assertThat(Double.parseDouble(reader.getRow(0).get(1)), is(equalTo(76378.0)));
  }

  @Test
  public void whenCsvColumnReadDataCorrect() throws Exception {
    assertThat(Double.parseDouble(reader.getColumn(1).get(0)), is(equalTo(76378.0)));
  }

  @Test
  public void whenCsvFileReturnedTheExpectedFileIsSent() {
    assertThat(reader.csvFile().getName(), is(equalTo(csvPath)));
  }

  @Test
  public void whenColumnByNameThenRightColumnDataReturned() {
    List<String> column = reader.getColumn("Pigs Slaughtered");
    assertThat(Double.parseDouble(column.get(0)), is(equalTo(76378.0)));
  }

}
