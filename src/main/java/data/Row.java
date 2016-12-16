package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacob on 12/13/16.
 */
public class Row {

  private int rowIndex = 0;
  private Map<Integer, RowInfo<?>> rowInfoMap = new HashMap<>();
  private Map<RowInfo<?>, Object> typeMap = new HashMap<>();

  public <T> void put(Class<T> type, T instance) {
    if (type == null) {
      throw new NullPointerException("Type is null.");
    }
    RowInfo<T> info = new RowInfo<>(rowIndex, type);
    rowInfoMap.put(rowIndex++, info);
    typeMap.put(info, instance);
  }

  public <T> T get(final int i, final Class<T> type) {
    RowInfo<T> info = new RowInfo<>(i, type);
    return info.clazz.cast(typeMap.get(info));
  }

  private static class RowInfo<T> {

    private final int rowIndex;
    private final Class<T> clazz;

    RowInfo(final int rowIndex, final Class<T> clazz) {
      this.rowIndex = rowIndex;
      this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RowInfo<?> rowInfo = (RowInfo<?>) o;

      if (rowIndex != rowInfo.rowIndex) return false;
      return clazz.equals(rowInfo.clazz);
    }

    @Override
    public int hashCode() {
      int result = rowIndex;
      result = 31 * result + clazz.hashCode();
      return result;
    }
  }

}
