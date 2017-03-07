package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.crypto.Data;
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
  public void whenRemoveColumnThenNoLongerThere() {
    Double[] data = new Double[] {4.5, 3.0, 1.5};
    DataFrame df = new DataFrame();
    df.add("0", Double[].class, data);
    df.remove("0", Double[].class);
  }
//
  @Test
  public void whenNullClassWithArrayAddThenException() {
    Double[] data = new Double[] {4.5, 3.0, 1.5};
    DataFrame df = new DataFrame();
    exception.expect(NullPointerException.class);
    df.add("0", null, data);
  }

  @Test
  public void whenNullClassWithListAddThenException() {
    Double[] data = new Double[] {4.5, 3.0, 1.5};
    DataFrame df = new DataFrame();
    exception.expect(NullPointerException.class);
    df.add("0", null, Arrays.asList(data));
  }

  @Test
  public void whenGetByIndexAndClassThenResultCorrect() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    df.add("0", Double[].class, doubles);
    Double[] result = df.get("0", Double[].class);
    assertThat(result, is(doubles));
  }

  @Test
  public void whenGetListThenResultCorrect() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    df.add("0", Double[].class, doubles);
    List<Double> result = df.getList("0", Double[].class);
    assertThat(result, is(Arrays.asList(doubles)));
  }

  @Test
  public void whenGetWithNullClassThenNullPointerException() {
    DataFrame df = new DataFrame();
    Double[] doubles = new Double[] {4.5, 3.0, 1.5};
    exception.expect(NullPointerException.class);
    df.add("0", null, doubles);
  }

  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2;
    DataFrame df = new DataFrame();
    DataFrame df2 = new DataFrame();

    assertThat(df, is(df));
    assertThat(df, is(df2));
    assertThat(df, is(not(stringData)));
    assertThat(df.hashCode(), is(df2.hashCode()));

    stringData2 = Arrays.asList("1.5", "-4.0");
    df2 = new DataFrame();
    df2.add("0", String[].class, stringData2);
    assertThat(df, is(not(df2)));
    assertThat(df.equals(null), is(false));

    DataFrame.ColumnInfo<String> columnInfo = new DataFrame.ColumnInfo<>("0", String[].class);
    assertThat(columnInfo, is(df2.getColumnInfo("0")));
    assertThat(columnInfo, is(not(df.getColumnInfo("0"))));
    assertThat(columnInfo, is(not(new Object())));
    DataFrame.ColumnInfo<Double> colInfo = new DataFrame.ColumnInfo<>("0", Double[].class);
    assertThat(colInfo, is(not(columnInfo)));
    assertThat(colInfo, is(colInfo));
    columnInfo = new DataFrame.ColumnInfo<>("1", String[].class);
    assertThat(columnInfo, is(not(df2.getColumnInfo("0"))));
  }
}
