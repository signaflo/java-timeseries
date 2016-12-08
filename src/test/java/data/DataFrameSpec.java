package data;

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
    Column<String> col = new Column<>(data);
    Column<Double> expected = new Column<>(Arrays.asList(3.0, 1.5, -4.0));
    DataFrame df = new DataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsDouble(0), is(expected));
  }

  @Test
  public void whenDataFramColumnAsStringThenStringColumnReturned() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    Column<String> expected = new Column<>(Arrays.asList("3.0", "1.5", "-4.0"));
    DataFrame df = new DataFrame(Collections.singletonList(col));
    assertThat(df.getColumnAsString(0), is(expected));
  }

  @Test
  public void whenColumnRemovedThenNoLongerPresent() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    DataFrame df = new DataFrame(Collections.singletonList(new Column<>(stringData)));
    List<Column<?>> columns = new ArrayList<>(2);
    DataFrame df2 = new DataFrame();
    df2.add(new Column<>(stringData));
    df2.add(new Column<>(doubleData));
    df2.removeColumn(1);
    assertThat(df, is(df2));
  }

  @Test
  public void whenGetColumnCalledDataFrameUnchanged() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    DataFrame df = new DataFrame(Collections.singletonList(new Column<>(stringData)));
    List<Column<?>> columns = new ArrayList<>(2);
    DataFrame df2 = new DataFrame();
    df2.add(new Column<>(stringData));
    df2.getColumn(0);
    assertThat(df, is(df2));
  }

  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    List<Double> doubleData2 = Arrays.asList(3.0, 1.5, -4.0);
    DataFrame df = new DataFrame();
    DataFrame df2 = new DataFrame();

    df.add(new Column<>(stringData)); df.add(new Column<>(doubleData));
    df2.add(new Column<>(stringData2)); df2.add(new Column<>(doubleData2));
    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    doubleData2 = Arrays.asList(3.0, -4.0);
    df2 = new DataFrame();
    df2.add(new Column<>(stringData2)); df.add(new Column<>(doubleData2));
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));
  }
}
