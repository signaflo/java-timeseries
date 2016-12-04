package data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 12/4/16.
 */
public class DataColumn<T> {

  private final List<T> data;

  public DataColumn(final List<T> data) {
    this.data = data;
  }

  public final List<T> getData() {
    return this.data;
  }

  public final T get(final int i) {
    return data.get(i);
  }

  public DataColumn<String> toStringColumn() {
    if (this.size() > 0 && get(0).getClass() == String.class) {
      return (DataColumn<String>) this;
    }
    List<String> stringData = new ArrayList<>(data.size());
    for (T t : data) {
      stringData.add(t.toString());
    }
    return new DataColumn<>(stringData);
  }

  public DataColumn<Double> toDoubleColumn() {
    if (this.size() > 0 && get(0).getClass() == Double.class) {
      return (DataColumn<Double>) this;
    }
    List<Double> doubleData = new ArrayList<>(data.size());
    for (T t : data) {
      String s = t.toString();
      if (TypeConverter.isDouble(s)) {
        doubleData.add(Double.valueOf(s));
      } else {
        throw new NotADoubleException(s + " could not be parsed as a Double.");
      }
    }
    return new DataColumn<>(doubleData);
  }

  public final int size() {
    return this.data.size();
  }
}
