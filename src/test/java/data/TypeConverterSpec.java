package data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by jacob on 12/4/16.
 */
public class TypeConverterSpec {

  @Test
  public void whenStringIsDoubleThenTrue() {
    String pi = "3.14";
    assertThat(TypeConverter.isDouble(pi), is(true));
  }

  @Test
  public void whenStringIsNotDoubleThenFalse() {
    String pi = "pi";
    assertThat(TypeConverter.isDouble(pi), is(false));
    pi = "3.41l";
    assertThat(TypeConverter.isDouble(pi), is(false));
  }

  @Test
  public void whenStringListToDoubleListThenDoublesReturned() {
    List<String> strings = new ArrayList<>(3);
    strings.add("3.5f");
    strings.add("-400.8f");
    strings.add("0.43");
    List<Double> ds = new ArrayList<>(3);
    ds.add(3.5);
    ds.add(-400.8);
    ds.add(0.43);
    assertThat(ds, is(TypeConverter.toDoubleList(strings)));
  }

  @Test
  public void testDataFrame() {
    List<String> strings = new ArrayList<>(3);
    strings.add("3.5l");
    strings.add("-400.8f");
    strings.add("0.43");
    List<Double> ds = new ArrayList<>(3);
    ds.add(3.5);
    ds.add(-400.8);
    ds.add(0.43);
    DataFrame df = new DataFrame();
    Column<String> stringColumn = new Column<>(strings);
    Column<Double> doubleColumn = new Column<>(ds);
    df.add(stringColumn);
    df.add(doubleColumn);
    doubleColumn = df.getColumn(0).asDouble();
    doubleColumn = df.getColumn(1).asDouble();

  }
}
