package data;

import com.sun.prism.PixelFormat;
import org.junit.Test;

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

  @Test
  public void whenDataFrameColumnAsDoubleThenDoubleColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column col = new Column(data);
    Column expected = new Column(Arrays.asList("3.0", "1.5", "-4.0"), DataType.DOUBLE);
    DataFrame df = new DataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsDouble(0), is(expected));
  }
//
  @Test
  public void whenDataFrameColumnAsStringThenStringColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column col = new Column(data);
    Column expected = new Column(Arrays.asList("3.0", "1.5", "-4.0"), DataType.STRING);
    DataFrame df = new DataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsString(0), is(expected));
  }
//
  @Test
  public void whenFixedDataFrameColumnAsDoubleThenDoubleColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column col = new Column(data, DataType.STRING);
    Column expected = new Column(data, DataType.DOUBLE);
    FixedDataFrame df = new FixedDataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsDouble(0), is(expected));
  }

  @Test
  public void whenFixedDataFrameColumnAsStringThenStringColumnReturned() {
    List<String> data = Arrays.asList("3.0", "1.5", "-4.0");
    Column col = new Column(data);
    Column expected = new Column(data, DataType.STRING);
    FixedDataFrame df = new FixedDataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsString(0), is(expected));
  }

  @Test
  public void whenColumnRemovedThenNoLongerPresent() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    DataFrame df = new DataFrame(Collections.singletonList(new Column(stringData, DataType.STRING)));
    DataFrame df2 = new DataFrame();
    df2.add(new Column(stringData, DataType.STRING));
    df2.add(new Column(stringData));
    df2.removeColumn(1);
    assertThat(df, is(df2));
  }

  @Test
  public void whenGetColumnCalledDataFrameUnchanged() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    DataFrame df = new DataFrame(Collections.singletonList(new Column(stringData)));
    DataFrame df2 = new DataFrame();
    df2.add(new Column(stringData));
    Column retrievedColumn = df2.getColumn(0);
    assertThat(retrievedColumn, is(new Column(stringData)));
    assertThat(df, is(df2));
  }

  @Test
  public void whenGetColumnCalledFixedDataFrameUnchanged() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<Column> columns = new ArrayList<>(2);
    columns.add(new Column(stringData, DataType.STRING));
    columns.add(new Column(stringData));
    FixedDataFrame df2 = new FixedDataFrame(columns);
    Column retrievedColumn = df2.getColumn(0);
    assertThat(retrievedColumn, is(new Column(stringData, DataType.STRING)));
  }

  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    DataFrame df = new DataFrame();
    DataFrame df2 = new DataFrame();

    df.add(new Column(stringData, DataType.STRING)); df.add(new Column(stringData, DataType.DOUBLE));
    df2.add(new Column(stringData2, DataType.STRING)); df2.add(new Column(stringData2, DataType.DOUBLE));
    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    df2 = new DataFrame();
    df2.add(new Column(stringData2, DataType.STRING)); df.add(new Column(stringData2, DataType.DOUBLE));
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));
  }

  @Test
  public void testEqualsAndHashCodeFixedDataFrame() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    FixedDataFrame df = new FixedDataFrame(Arrays.asList(new Column(stringData, DataType.STRING),
        new Column(stringData, DataType.DOUBLE)));
    FixedDataFrame df2 = new FixedDataFrame(Arrays.asList(new Column(stringData2, DataType.STRING),
        new Column(stringData2, DataType.DOUBLE)));

    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    df2 = new FixedDataFrame(Arrays.asList(new Column(stringData2, DataType.STRING),
        new Column(stringData2, DataType.DOUBLE)));
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));
  }
}
