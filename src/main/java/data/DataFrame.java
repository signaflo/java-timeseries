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

import java.lang.reflect.Array;
import java.util.*;

/**
 *
 * A potentially heteregoneous collection of {@link Column}s of data. This class is mutable and not thread-safe. The
 * immutable and thread-safe version of this class is {@link FixedDataFrame}.
 *
 * @author Jacob Rachiele
 * Date: Dec 07 2016
 */
public class DataFrame {

  private int colIndex;
  private final Map<Integer, Class<?>> columnIndexMap;
  private final Map<ColumnInfo<?>, Object> columnMap;

  /**
   * Create a new empty dataframe.
   */
  public DataFrame() {
    this.columnIndexMap = new HashMap<>();
    this.columnMap = new HashMap<>();
  }

  /**
   * Add the array of the given type to the data frame.
   * @param type the class type of the data in the array.
   * @param instance the array of data.
   * @param <T> the type of the data in the array.
   */
  public <T> void add(Class<T[]> type, T[] instance) {
    if (type == null) {
      throw new NullPointerException("The class type was null.");
    }
    columnIndexMap.put(colIndex, type);
    ColumnInfo<T> colInfo = new ColumnInfo<>(colIndex++, type);
    columnMap.put(colInfo, instance);
  }

  public <T> void add(Class<T[]> type, List<T> instance) {
    if (type == null) {
      throw new NullPointerException();
    }
    columnIndexMap.put(colIndex, type);
    T[] asArray = type.cast(instance.toArray());
    ColumnInfo<T> colInfo = new ColumnInfo<T>(colIndex++, type);
    columnMap.put(colInfo, asArray);
  }

  /**
   * Remove and return the ith column from this dataframe.
   * @param i the index of the column to remove.
   * @param type the class type of the column to remove.
   * @param <T> the type of the data in the column to remove.
   * @return the removed column.
   */
  public <T> T[] removeColumn(final int i, Class<T[]> type) {
    return type.cast(this.columnMap.remove(new ColumnInfo<>(i, type)));
  }

  /**
   * Retrieve the data in the ith column as a list of the given type.
   * @param i the index of the column to retrieve.
   * @param type the class type of the data in the column.
   * @param <T> the type of the data in the column.
   * @return the data in the ith column as a list of the given type.
   */
  public <T> List<T> getList(final int i, final Class<T[]> type) {
    ColumnInfo<T> info = new ColumnInfo<>(i, type);
    T[] column = get(info);
    return Arrays.asList(column);
  }

  public Class<?> getColumnClass(final int i) {
    return columnIndexMap.get(i);
  }

  public String getColumnClassName(final int i) {
    return columnIndexMap.get(i).getSimpleName();
  }

  /**
   * Retrieve the data in the ith column as an array of the given type.
   * The array class type is required for type safety. For an example of how to use this method,
   * Double[] columnData = ne w
   * @param i the index of the column to retrieve.
   * @param type the class type of the data in the array.
   * @param <T> the type of the data in the array.
   * @return the data in the ith column as an array of the given type.
   */
  public <T> T[] get(final int i, final Class<T[]> type) {
    ColumnInfo<T> info = new ColumnInfo<>(i, type);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DataFrame dataFrame = (DataFrame) o;

    if (colIndex != dataFrame.colIndex) return false;
    if (!columnIndexMap.equals(dataFrame.columnIndexMap)) return false;
    return columnMap.equals(dataFrame.columnMap);
  }

  @Override
  public int hashCode() {
    int result = colIndex;
    result = 31 * result + columnIndexMap.hashCode();
    result = 31 * result + columnMap.hashCode();
    return result;
  }

  /**
   * Print the string representation of this dataframe to the console.
   */
  public void print() {
    System.out.println(toString());
  }

  static class ColumnInfo<T> {

    private final int columnIndex;
    private final Class<T[]> clazz;

    ColumnInfo(final int columnIndex, final Class<T[]> clazz) {
      this.columnIndex = columnIndex;
      this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ColumnInfo<?> columnInfo = (ColumnInfo<?>) o;

      if (columnIndex != columnInfo.columnIndex) return false;
      return clazz.equals(columnInfo.clazz);
    }

    @Override
    public int hashCode() {
      int result = columnIndex;
      result = 31 * result + clazz.hashCode();
      return result;
    }
  }
}
