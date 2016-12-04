package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 12/4/16.
 */
public class DataFrame {

  private List<DataColumn<?>> data;

  public DataFrame() {
    data = new ArrayList<>();
  }

  public DataFrame(final List<DataColumn<?>> data) {
    this.data = data;
  }

  public void add(DataColumn<?> column) {
    data.add(column);
  }

  public DataColumn<?> removeColumn(final int i) {
    return this.data.remove(i);
  }

  public DataColumn<?> getColumn(final int i) {
    return this.data.get(i);
  }
}
