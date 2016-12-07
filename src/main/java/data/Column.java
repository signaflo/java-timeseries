package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jacob on 12/4/16.
 */
public class Column<T> {

  private final List<T> data;
  private final Class<T> clazz;

  public Column(final List<T> data) {
    this.clazz = inferType(data);
    this.data = Collections.unmodifiableList(data);
  }

  @SuppressWarnings("unchecked")
  private Class<T> inferType(List<T> data) {
    if (data.size() == 0) {
      return (Class<T>)Object.class;
    }

    T item = data.get(0);
    if (item.getClass().equals(Double.class)) {
      return (Class<T>)Double.class;
    } else if (item.getClass().equals(String.class)){
      return (Class<T>)String.class;
    } else {
      return (Class<T>)Object.class;
    }
  }

  public Column(final List<T> data, final Class<T> clazz) {
    this.clazz = clazz;
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
