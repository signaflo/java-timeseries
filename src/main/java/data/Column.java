package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jacob on 12/4/16.
 */
public class Column<T> {

  private final List<T> data;

  public Column(final List<T> data) {
    this.data = Collections.unmodifiableList(data);
  }

  public final List<T> getData() {
    return this.data;
  }

  public final T get(final int i) {
    return data.get(i);
  }

  public Column<String> asString() {
    List<String> stringData = new ArrayList<>(data.size());
    for (T t : data) {
      stringData.add(t.toString());
    }
    return new Column<>(stringData);
  }

  public Column<Double> asDouble() {
    List<Double> doubleData = new ArrayList<>(data.size());
    for (T t : data) {
      String s = t.toString();
      if (TypeConversion.isDouble(s)) {
        doubleData.add(Double.valueOf(s));
      } else {
        throw new NotADoubleException(s + " could not be parsed as a Double.");
      }
    }
    return new Column<>(doubleData);
  }

  public final int size() {
    return this.data.size();
  }
}
