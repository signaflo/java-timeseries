package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by jacob on 12/8/16.
 */
public class DataFrameSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenFixedDataFrameColumnAsDoubleThenDoubleColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column<String> col = new Column<>(data);
    Column<Double> expected = new Column<>(Arrays.asList(3.0, 1.5, -4.0));
    FixedDataFrame df = new FixedDataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsDouble(0), is(expected));
  }

  @Test
  public void whenFixedDataFramColumnAsStringThenStringColumnReturned() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    Column<String> expected = new Column<>(Arrays.asList("3.0", "1.5", "-4.0"));
    FixedDataFrame df = new FixedDataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsString(0), is(expected));
  }

  @Test
  public void whenGetColumnCalledFixedDataFrameUnchanged() throws Exception {
    Row row = new Row();
    row.put(Double.class, 3.5);
    row.get(0, Double.class);
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    List<Column<?>> columns = new ArrayList<>(2);
    columns.add(new Column<>(stringData));
    columns.add(new Column<>(doubleData));
    FixedDataFrame df2 = new FixedDataFrame(columns);
    Column<?> retrievedColumn = df2.getColumn(0);
    assertThat(retrievedColumn, is(new Column<>(stringData)));
  }

  @Test
  public void whenGetByIndexAndClassThenResultCorrect() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    df.add(Double[].class, doubles);
    Double[] result = df.get(0, Double[].class);
    assertThat(result, is(doubles));
  }

  @Test
  public void whenGetListThenResultCorrect() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    df.add(Double[].class, doubles);
    System.out.println(df.getColumnClass(0));
    System.out.println(df.getColumnClassName(0));
    List<Double> result = df.getList(0, Double[].class);
    assertThat(result, is(Arrays.asList(doubles)));
  }

  @Test
  public void whenGetWithNullClassThenNullPointerException() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    exception.expect(NullPointerException.class);
    df.add(null, doubles);
  }

  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    List<Double> doubleData2 = Arrays.asList(3.0, 1.5, -4.0);
    DataFrame df = new DataFrame();
    DataFrame df2 = new DataFrame();

    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    doubleData2 = Arrays.asList(3.0, -4.0);
    df2 = new DataFrame();
    df2.add(String[].class, stringData2);
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));
  }

  @Test
  public void testEqualsAndHashCodeFixedDataFrame() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    List<Double> doubleData2 = Arrays.asList(3.0, 1.5, -4.0);
    FixedDataFrame df = new FixedDataFrame(Arrays.asList(new Column<>(stringData), new Column<>(doubleData)));
    FixedDataFrame df2 = new FixedDataFrame(Arrays.asList(new Column<>(stringData2), new Column<>(doubleData2)));

    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    doubleData2 = Arrays.asList(3.0, -4.0);
    df2 = new FixedDataFrame(Arrays.asList(new Column<>(stringData2), new Column<>(doubleData2)));
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));
  }
}
