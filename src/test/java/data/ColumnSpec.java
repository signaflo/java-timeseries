package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacob on 12/7/16.
 */
public class ColumnSpec {

  private List<String> data;

  @Before
  public void beforeMethod() {
    data = new ArrayList<>(3);
    data.add("3.0"); data.add("1.5"); data.add("-4.0");
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenModifyReturnedDataThenObjectDataUnchanged() {
    Column col = new Column(data, DataType.STRING);
    List<String> colData = col.data();
    exception.expect(UnsupportedOperationException.class);
    colData.remove(2);
  }

  @Test
  public void whenColumnWithEmptyDataThenTypeIsString() {
    Column col = new Column(new ArrayList<>());
    assertThat(col.dataType(), is(equalTo(DataType.STRING)));
  }

  @Test
  public void whenColumnWithDoubleDataThenTypeIsDouble() {
    Column col = new Column(data, DataType.DOUBLE);
    assertThat(col.dataType(), is(equalTo(DataType.DOUBLE)));
  }

  @Test
  public void whenColumnWithStringDataThenTypeIsString() {
    data = Arrays.asList("A", "B", "C");
    Column col = new Column(data);
    assertThat(col.dataType(), is(equalTo(DataType.STRING)));
  }

  @Test
  public void whenDoubleColumnThenSimpleTypeNameIsDouble() {
    Column col = new Column(data, DataType.DOUBLE);
    assertThat(col.typeName(), is(equalTo("Double")));
  }

  @Test
  public void whenStringColumnAsDoubleThenDoubleColumnReturned() {
    Column col = new Column(data, DataType.STRING);
    Column expected = new Column(data, DataType.DOUBLE);
    assertThat(col.asDouble(), is(expected));
  }

  @Test
  public void whenDoubleColumnAsStringThenStringColumnReturned() {
    Column col = new Column(data, DataType.DOUBLE);
    Column expected = new Column(Arrays.asList("3.0", "1.5", "-4.0"), DataType.STRING);
    assertThat(col.asString(), is(expected));
  }

  @Test
  public void whenGetThenValueReturned() throws Exception {
    Column col = new Column(data, DataType.DOUBLE);
    assertThat(col.get(1), is("1.5"));
    data = new ArrayList<>(3);
    String dateTime = "2016-05-07T10:30:35-06:00";
    data.add(dateTime);
    col = new Column(data, DataType.OFFSET_DATE_TIME);
    assertThat(col.dataType().convert(col.get(0)), is(OffsetDateTime.parse(dateTime)));
  }

  @Test
  public void whenSizeThenSizeReturned() {
    Column col = new Column(data);
    assertThat(col.size(), is(3));
  }
  @Test
  public void testEqualsAndHashCode() {
    List<String> stringData = Arrays.asList("3.0", "1.5", "-4.0");
    List<String> stringData2 = Arrays.asList("3.0", "1.5", "-4.0");
    Column stringColumn = new Column(stringData);
    Column doubleColumn = new Column(stringData, DataType.DOUBLE);
    Column stringColumn2 = new Column(stringData2);
    Column doubleColumn2 = new Column(stringData2, DataType.DOUBLE);

    assertThat(stringColumn, is(stringColumn));
    assertThat(stringColumn, is(stringColumn2));
    assertThat(stringColumn, is(not(new Object())));
    assertThat(stringColumn.hashCode(), is(stringColumn2.hashCode()));

    stringColumn2 = new Column(Arrays.asList("1.5", "-4.0"));

    assertThat(stringColumn, is(not(stringColumn2)));
    assertThat(stringColumn.equals(null), is(false));
  }
}
