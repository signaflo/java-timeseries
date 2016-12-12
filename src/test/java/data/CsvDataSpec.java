package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
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
    assertThat(Double.parseDouble(column.get(0)), is(equalTo(76378.0)));
  }

  @Test
  public void whenCreateDataFrameThenExpectedDataFrameReturned() {
    Column stringColumn = new Column(Arrays.asList("A", "B", "C"));
    Column doubleColumn = new Column(Arrays.asList("1.0", "2.5", "-3.0"));
    DataFrame expected = new DataFrame(Arrays.asList(stringColumn, doubleColumn));

    csvPath = "test-data.csv";
    csvData = new CsvData(csvPath);
    List<DataType> types = new ArrayList<>(2);
    types.add(DataType.STRING); types.add(DataType.STRING);
    DataFrame df = csvData.createDataFrame(types);
    assertThat(df, is(expected));
    df = csvData.createDataFrame();
    assertThat(df, is(expected));
  }

  @Test
  public void whenCreateFixedDataFrameThenExpectedDataFrameReturned() {
    Column stringColumn = new Column(Arrays.asList("A", "B", "C"), DataType.STRING);
    Column doubleColumn = new Column(Arrays.asList("1.0", "2.5", "-3.0"), DataType.STRING);
    FixedDataFrame expected = new FixedDataFrame(Arrays.asList(stringColumn, doubleColumn));

    csvPath = "test-data.csv";
    csvData = new CsvData(csvPath);
    List<DataType> types = new ArrayList<>(2);
    types.add(DataType.STRING); types.add(DataType.STRING);
    FixedDataFrame df = csvData.createFixedDataFrame(types);
    assertThat(df, is(expected));
    df = csvData.createFixedDataFrame();
    assertThat(df, is(expected));
  }

  @Test
  public void whenCreateDataFrameWithWrongNumberOfClassesThenException() {
    List<DataType> types = new ArrayList<>(1);
    types.add(DataType.STRING);
    exception.expect(IllegalArgumentException.class);
    csvData.createDataFrame(types);
  }


  @Test
  public void whenCreateFixedDataFrameWithWrongNumberOfTypesThenException() {
    Column stringColumn = new Column(Arrays.asList("A", "B", "C"));
    Column doubleColumn = new Column(Arrays.asList("1.0", "2.5", "-3.0"));

    List<DataType> types = new ArrayList<>(1);
    types.add(DataType.STRING);
    exception.expect(IllegalArgumentException.class);
    csvData.createFixedDataFrame(types);
  }

  @Test
  public void whenEmptyCSVThenEmptyDataFrame() {

    csvPath = "2015-03-01.csv";
    List<DataType> schema = Arrays.asList(DataType.STRING, DataType.OFFSET_DATE_TIME, DataType.DOUBLE,
        DataType.STRING, DataType.STRING, DataType.DOUBLE, DataType.DOUBLE, DataType.STRING);
    DataFrame dataFrame = new CsvData(csvPath, true).createDataFrame(schema);
    DataFrame df = new DataFrame();
    FixedDataFrame fdf = new FixedDataFrame();
    csvPath = "empty.csv";
    csvData = new CsvData(csvPath);
    assertThat(csvData.createDataFrame(), is(df));
    assertThat(csvData.createFixedDataFrame(), is(fdf));
    assertThat(csvData.createDataFrame(Collections.emptyList()), is(df));
    assertThat(csvData.createFixedDataFrame(Collections.emptyList()), is(fdf));
  }

}
