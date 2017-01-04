/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package data;

import java.util.*;

/**
 *
 * A potentially heterogeneous collection of columns of data. This class is mutable and not thread-safe. The
 * immutable and thread-safe version of this class is {FixedDataFrame}.
 *
 * @author Jacob Rachiele
 * Date: Dec 07 2016
 */
public final class DataFrame {

  private final Map<String, ColumnInfo<?>> columnIdMap;
  private final Map<ColumnInfo<?>, Object> columnMap;

  /**
   * Create a new empty dataframe with the default column capacity. The column capacity will grow and
   * shrink as necessary.
   */
  public DataFrame() {
    this.columnIdMap = new HashMap<>();
    this.columnMap = new HashMap<>();
  }

  /**
   * Create a new empty dataframe with the given initial column capacity. The column capacity will grow and shrink
   * as necessary.
   * @param numColumns the initial column capacity.
   */
  public DataFrame(final int numColumns) {
    this.columnIdMap = new HashMap<>(numColumns);
    this.columnMap = new HashMap<>(numColumns);
  }

  public final int size() {
    return this.columnMap.size();
  }

  public Set<String> getColumnIds() {
    return this.columnIdMap.keySet();
  }

  /**
   * Add the array of the given type to the data frame.
   * @param id the unique id of the new column.
   * @param type the class type of the data in the array.
   * @param instance the array of data.
   * @param <T> the type of the data in the array.
   */
  public final <T> void add(final String id, final Class<T[]> type, final T[] instance) {
    if (type == null) {
      throw new NullPointerException("The class type was null.");
    }
    if (columnIdMap.containsKey(id)) {
      throw new IllegalArgumentException("The column id, " + id + ", already exists. The column id must be unique.");
    }
    ColumnInfo<T> colInfo = new ColumnInfo<>(id, type);
    columnIdMap.put(id, colInfo);
    columnMap.put(colInfo, instance);
  }

  /**
   * Add the array of the given type to the data frame.
   * @param id the unique id of the new column.
   * @param type the class type of the data in the array.
   * @param instance the array of data.
   * @param <T> the type of the data in the array.
   */
  public final <T> void replace(final String id, final Class<T[]> type, final T[] instance) {
    if (type == null) {
      throw new NullPointerException("The class type was null.");
    }
    if (!columnIdMap.containsKey(id)) {
      throw new IllegalArgumentException("The column id, " + id + ", doesn't exist, so there is nothing to replace.");
    }
    ColumnInfo<?> oldInfo = columnIdMap.get(id);
    columnMap.remove(oldInfo);
    ColumnInfo<T> colInfo = new ColumnInfo<>(id, type);
    columnIdMap.put(id, colInfo);
    columnMap.put(colInfo, instance);
  }

  /**
   * Add the array of the given type to the data frame.
   * @param id the id of the new column.
   * @param type the class type of the data in the array.
   * @param instance the list of data.
   * @param <T> the type of the data in the list.
   */
  public <T> void add(final String id, Class<T[]> type, List<T> instance) {
    if (type == null) {
      throw new NullPointerException("The class type was null.");
    }
    T[] asArray = type.cast(instance.toArray());
    ColumnInfo<T> colInfo = new ColumnInfo<>(id, type);
    columnIdMap.put(id, colInfo);
    columnMap.put(colInfo, asArray);
  }

  /**
   * Remove and return the specified column from this dataframe.
   * @param id the identifier of the column to remove.
   * @param type the class type of the column to remove.
   * @param <T> the type of the data in the column to remove.
   * @return the removed column.
   */
  public <T> T[] remove(final String id, Class<T[]> type) {
    return type.cast(this.columnMap.remove(new ColumnInfo<>(id, type)));
  }

  /**
   * Retrieve the data in the specified column as a list of the given type.
   * @param id the identifier of the column to retrieve.
   * @param type the class type of the data in the column.
   * @param <T> the type of the data in the column.
   * @return the data in the ith column as a list of the given type.
   */
  public <T> List<T> getList(final String id, final Class<T[]> type) {
    ColumnInfo<T> info = new ColumnInfo<>(id, type);
    T[] column = get(info);
    return Arrays.asList(column);
  }

  public ColumnInfo<?> getColumnInfo(final String id) {
    return this.columnIdMap.get(id);
  }

  public Class<?> getColumnClass(final String id) {
    return columnIdMap.get(id).clazz;
  }

  public String getColumnClassName(final String id) {
    return columnIdMap.get(id).clazz.getSimpleName();
  }

  /**
   * Retrieve the data in the specified column as an array of the given type.
   * The array class type is required for type safety. For an example of how to use this method,
   * Double[] columnData = ne w
   * @param id the identifier of the column to retrieve.
   * @param type the class type of the data in the array.
   * @param <T> the type of the data in the array.
   * @return the data in the ith column as an array of the given type.
   */
  public <T> T[] get(final String id, final Class<T[]> type) {
    ColumnInfo<T> info = new ColumnInfo<>(id, type);
    return get(info);
  }

  /**
   * Retrieve a column of data from the data frame using the provided column information.
   * @param info the column information to use to retrieve the data.
   * @param <T> the type of the data in the array.
   * @return the data in the ith column as an array of the given type.
   */
  public <T> T[] get(final ColumnInfo<T> info) {
    return info.clazz.cast(columnMap.get(info));
  }

//  @Override
//  public String toString() {
//    StringBuilder sb = new StringBuilder();
//    final int nrows = (data.size() > 0)? data.get(0).size() : 0;
//    final int fixedWidth = 15;
//    Column<?> columnData;
//    String s;
//    for (int i = 0; i < data.size(); i++) {
//      s = "Type:" + data.get(i).getSimpleTypeName();
//      sb.append(String.format("%-" + fixedWidth + "." + fixedWidth + "s", s));
//      sb.append("|");
//    }
//    sb.append("\n");
//    for (int j = 0; j < nrows; j++) {
//      for (int i = 0; i < data.size(); i++) {
//        columnData = data.get(i);
//        sb.append(String.format("%-" + fixedWidth + "." + fixedWidth + "s", columnData.get(j)));
//        sb.append("|");
//      }
//      if (j != nrows - 1) {
//        sb.append("\n");
//      }
//    }
//    return sb.toString();
//  }

  /**
   * Print the string representation of this dataframe to the console.
   */
  public void print() {
    System.out.println(toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DataFrame dataFrame = (DataFrame) o;

    if (!columnIdMap.equals(dataFrame.columnIdMap)) return false;
    return columnMap.equals(dataFrame.columnMap);
  }

  @Override
  public int hashCode() {
    int result = columnIdMap.hashCode();
    result = 31 * result + columnMap.hashCode();
    return result;
  }

  public static class ColumnInfo<T> {

    private final String columnId;
    private final Class<T[]> clazz;

    public ColumnInfo(final String columnId, final Class<T[]> clazz) {
      this.columnId = columnId;
      this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ColumnInfo<?> that = (ColumnInfo<?>) o;

      if (!columnId.equals(that.columnId)) return false;
      return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
      int result = columnId.hashCode();
      result = 31 * result + clazz.hashCode();
      return result;
    }
  }
}
