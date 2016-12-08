package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacob on 12/7/16.
 */
public class ColumnSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testColumn() {
    String csvPath = "monthly-total-number-of-pigs-sla.csv";
    CsvReader reader = new CsvReader(csvPath, true);
    DataFrame df = reader.createDataFrame();
    Column<?> col = df.getColumn(0);
    Column<?> col2 = df.getColumn(1);
    System.out.println(col.getTypeName());
    System.out.println(col2.getTypeName());
  }

  @Test
  public void whenModifyReturnedDataThenObjectDataUnchanged() {
    exception.expect(UnsupportedOperationException.class);
    List<Double> data = new ArrayList<>(3);
    data.add(3.0); data.add(1.5); data.add(-4.0);
    Column<Double> col = new Column<>(data);
    List<Double> colData = col.getData();
    List<Double> columnData = new ArrayList<>(colData);
    columnData.remove(2);
    System.out.println(columnData);
    System.out.println(colData);
    colData.remove(2);
  }

  @Test
  public void whenColumnWithEmptyDataThenTypeIsObject() {
    Column<Double> col = new Column<>(new ArrayList<>());
    assertThat(col.getType(), is(equalTo(Object.class)));
  }

  @Test
  public void whenColumnWithDoubleDataThenTypeIsDouble() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    assertThat(col.getType(), is(equalTo(Double.class)));
  }

  @Test
  public void whenColumnWithStringDataThenTypeIsString() {
    List<String> data = Arrays.asList("A", "B", "C");
    Column<String> col = new Column<>(data);
    assertThat(col.getType(), is(equalTo(String.class)));
  }

  @Test
  public void whenStringColumnThenSimpleTypeNameIsString() {
    List<String> data = Arrays.asList("A", "B", "C");
    Column<String> col = new Column<>(data);
    assertThat(col.getSimpleTypeName(), is(equalTo("String")));
  }

  @Test
  public void whenStringColumnAsDoubleThenDoubleColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column<String> col = new Column<>(data);
    Column<Double> expected = new Column<>(Arrays.asList(3.0, 1.5, -4.0));
    assertThat(col.asDouble(), is(expected));
  }

  @Test
  public void whenDoubleColumnAsStringThenStringColumnReturned() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    Column<String> expected = new Column<>(Arrays.asList("3.0", "1.5", "-4.0"));
    assertThat(col.asString(), is(expected));
  }
}
