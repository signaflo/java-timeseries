package data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by jacob on 12/4/16.
 */
public class TypesSpec {

  @Test
  public void whenStringIsDoubleThenTrue() {
    String pi = "3.14";
    assertThat(Types.isDouble(pi), is(true));
  }

  @Test
  public void whenStringIsLocalDateTimeThenTrue() {
    String dt = "2016-12-31T02:22:35";
    //System.out.println(LocalDateTime.parse(dt));
    assertThat(Types.isLocalDateTime(dt), is(true));
  }

  @Test
  public void whenStringNotLocalDateTimeThenFalse() {
    String dt = "2016-15-10T02:22:35";
    //System.out.println(LocalDateTime.parse(dt));
    assertThat(Types.isLocalDateTime(dt), is(false));
    dt = "2016-12-44T02:22:35";
    assertThat(Types.isLocalDateTime(dt), is(false));
  }

  @Test
  public void whenStringIsOffsetDateTimeThenTrue() {
    String dt = "2015-09-29T02:22:35-13:30";
    //System.out.println(OffsetDateTime.parse(dt));
    assertThat(Types.isOffsetDateTime(dt), is(true));
  }

  @Test
  public void whenStringNotOffsetDateTimeThenFalse() {
    String dt = "2016-12-10T02:22:35";
    //System.out.println(LocalDateTime.parse(dt));
    assertThat(Types.isOffsetDateTime(dt), is(false));
  }

  @Test
  public void whenStringIsNotDoubleThenFalse() {
    String pi = "pi";
    assertThat(Types.isDouble(pi), is(false));
    pi = "3.41l";
    assertThat(Types.isDouble(pi), is(false));
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
    assertThat(ds, is(Types.toDoubleList(strings)));
  }

  @Test
  public void whenNotADoubleThenGoodMessage() {
    NotADoubleException e = new NotADoubleException();
    assertThat(e.getMessage(), is("An attempt was made to treat a non-Double object as a Double."));
  }
}
