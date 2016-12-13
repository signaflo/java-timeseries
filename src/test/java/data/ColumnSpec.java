package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacob on 12/7/16.
 */
public class ColumnSpec {

  private Double[] doubles;
  private String[] strings;

  @Before
  public void beforeMethod() {
    doubles = new Double[] {3.0, 1.5, -4.0};
    strings = new String[] {"A", "B", "C"};
  }
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenModifyReturnedDataThenObjectDataUnchanged() {
    Column<Double> col = new Column<>(doubles);
    List<Double> colData = Arrays.asList(col.getData());
    exception.expect(UnsupportedOperationException.class);
    colData.remove(2);
  }

  @Test
  public void whenNotADoubleThenAsDoubleThrowsException() {
    Column<String> col = new Column<>(strings);
    exception.expect(NotADoubleException.class);
    col.asDouble();
  }

  @Test
  public void whenColumnWithEmptyDataThenTypeIsObject() {
    Column<Double> col = new Column<>(new Double[] {});
    assertThat(col.getType(), is(equalTo(Object.class)));
  }

  @Test
  public void whenColumnWithDoubleDataThenTypeIsDouble() {
    Column<Double> col = new Column<>(doubles);
    assertThat(col.getType(), is(equalTo(Double.class)));
  }

  @Test
  public void whenColumnWithStringDataThenTypeIsString() {
    Column<String> col = new Column<>(strings);
    assertThat(col.getType(), is(equalTo(String.class)));
  }

  @Test
  public void whenColumnWithNonInferableDataTypeThenTypeIsObject() {
    List<StringBuilder> data = Arrays.asList(new StringBuilder(), new StringBuilder(), new StringBuilder());
    Column<StringBuilder> col = new Column<>(data);
    assertThat(col.getType(), is(equalTo(Object.class)));
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
    List<Number> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Number> col = new Column<>(data);
    Column<String> expected = new Column<>(Arrays.asList("3.0", "1.5", "-4.0"));
    assertThat(col.asString(), is(expected));
  }

  @Test
  public void whenGetThenValueReturned() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    assertThat(col.get(1), is(1.5));
  }

  @Test
  public void whenSizeThenSizeReturned() {
    List<Double> data = Arrays.asList(3.0, 1.5, -4.0);
    Column<Double> col = new Column<>(data);
    assertThat(col.size(), is(3));
  }
  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    List<Double> doubleData = Arrays.asList(3.0, 1.5, -4.0);
    List<Double> doubleData2 = Arrays.asList(3.0, 1.5, -4.0);
    Column<String> stringColumn = new Column<>(stringData);
    Column<Double> doubleColumn = new Column<>(doubleData);
    Column<String> stringColumn2 = new Column<>(stringData2);
    Column<Double> doubleColumn2 = new Column<>(doubleData2);

    assertThat(stringColumn, is(stringColumn));
    assertThat(stringColumn, is(stringColumn2));
    assertThat(stringColumn, is(not(new Object())));
    assertThat(stringColumn.hashCode(), is(stringColumn2.hashCode()));

    stringColumn2 = new Column<>(Arrays.asList("1.5", "-4.0"));

    assertThat(stringColumn, is(not(stringColumn2)));
    assertThat(stringColumn.equals(null), is(false));
  }
}
