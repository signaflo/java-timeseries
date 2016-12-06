package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 12/4/16.
 */
public class DataFrame {

  private final List<Column<?>> data;

  public DataFrame() {
    data = new ArrayList<>();
  }

  public DataFrame(final List<Column<?>> data) {
    this.data = data;
  }

  public void add(Column<?> column) {
    data.add(column);
  }

  public Column<?> removeColumn(final int i) {
    return this.data.remove(i);
  }

  public Column<?> getColumn(final int i) {
    return this.data.get(i);
  }

  public Column<Double> getColumnAsDouble(final int i) {
    return this.data.get(i).asDouble();
  }

  public Column<String> getColumnAsString(final int i) {
    return this.data.get(i).asString();
  }
}
