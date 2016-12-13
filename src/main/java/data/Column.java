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

import com.google.common.collect.ImmutableList;
import timeseries.TimeSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An immutable and thread-safe column of data. Objects of this class typically compose a {@link DataFrame}, but may
 * exist seperately. A column consists of the data itself together with a {@link Class} variable that acts as
 * metadata indicating the data's type. This is done to allow dataframes to hold columns of differing types.
 *
 * @author Jacob Rachiele
 * Date: Dec 07 2016
 *
 */
public final class Column<T> {

  private final T[] data;
  private final Class<T> type;

  /**
   * Create a new column from the list of input data. The class of the column will be inferred from the data.
   * @param data the column data.
   */
  public Column(final T[] data) {
    this.type = inferType(data);
    this.data = data.clone();
  }

  /**
   * Create a new column from the list of input data. The class of the column will be inferred from the data.
   * @param data the column data.
   */
  @SuppressWarnings("unchecked")
  public Column(final List<T> data) {
    this.type = inferType(data);
    this.data = data.toArray((T[])Array.newInstance(type, 0));
  }

  /**
   * Performs very simple type inference on the list of input data and returns the inferred class.
   * @param data the list of data whose type is to be inferred.
   * @return the inferred class.
   */
  @SuppressWarnings("unchecked")
  private Class<T> inferType(T[] data) {
    if (data.length == 0) {
      return (Class<T>)Object.class;
    }

    T item = data[0];
    /*if (item.getClass().getSuperclass().equals(Number.class)) {
      return (Class<T>)Number.class;
    } else */if (item.getClass().equals(Double.class)) {
      return (Class<T>)Double.class;
    } else if (item.getClass().equals(String.class)){
      return (Class<T>)String.class;
    } else {
      return (Class<T>)Object.class;
    }
  }

  /**
   * Performs very simple type inference on the list of input data and returns the inferred class.
   * @param data the list of data whose type is to be inferred.
   * @return the inferred class.
   */
  @SuppressWarnings("unchecked")
  private Class<T> inferType(List<T> data) {
    if (data.size() == 0) {
      return (Class<T>)Object.class;
    }

    T item = data.get(0);
    /*if (item.getClass().getSuperclass().equals(Number.class)) {
      return (Class<T>)Number.class;
    } else */if (item.getClass().equals(Double.class)) {
      return (Class<T>)Double.class;
    } else if (item.getClass().equals(String.class)){
      return (Class<T>)String.class;
    } else {
      return (Class<T>)Object.class;
    }
  }

  /**
   * Create a new column of the given type from the list of input data. The class variable is used so that
   * a {@link DataFrame} may hold columns of different types.
   * @param data the column data.
   * @param type the type of data the column contains.
   */
  public Column(final T[] data, final Class<T> type) {
    this.type = type;
    this.data = data;
  }

  /**
   * Create a new column of the given type from the list of input data. The class variable is used so that
   * a {@link DataFrame} may hold columns of different types.
   * @param data the column data.
   * @param type the type of data the column contains.
   */
  @SuppressWarnings("unchecked")
  public Column(final List<T> data, final Class<T> type) {
    this.type = type;
    this.data = data.toArray((T[])Array.newInstance(type, 0));
  }

  /**
   * Retrieve the list of data in the column.
   * @return the list of data in the column.
   */
  public final T[] getData() {
    return this.data;
  }

  /**
   * Retrieve the piece of data in the ith index position.
   * @param i the index position of the data in the column
   * @return the piece of data in the ith index position.
   */
  public final T get(final int i) {
    return data[i];
  }

  /**
   * Convert the data to a list of strings and return the result in a new column.
   * @return a new column of string data.
   */
  public Column<String> asString() {
    String[] stringData = new String[(data.length)];
    for (int i = 0; i < data.length; i++) {
      stringData[i] = data[i].toString();
    }
    return new Column<>(stringData);
  }

  /**
   * Convert the data to a list of (wrapped) doubles and return the result in a new column.
   * @return a new column of (wrapped) double data.
   */
  public Column<Double> asDouble() {
    List<Double> doubleData = new ArrayList<>(data.length);
    for (T t : data) {
      String s = t.toString();
      if (TypeConversion.isDouble(s)) {
        doubleData.add(Double.valueOf(s));
      } else {
        throw new NotADoubleException(s + " could not be parsed as a Double.");
      }
    }
    return new Column<>(doubleData.toArray(new Double[] {}));
  }

  /**
   * Retrieve the column type.
   * @return the column type.
   */
  public Class<T> getType() {
    return this.type;
  }

  /**
   * Retrieve the string representation of the column type.
   * @return the string representation of the column type.
   */
  public String getTypeName() {
    return this.type.getTypeName();
  }

  /**
   * Retrieve the simple string representation of the column type.
   * @return the simple string representation of the column type.
   */
  public String getSimpleTypeName() {
    return this.type.getSimpleName();
  }

  /**
   * Retrieve the size of the column.
   * @return the size of the column.
   */
  public final int size() {
    return this.data.length;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Column<?> column = (Column<?>) o;

    if (!Arrays.equals(data, column.data)) return false;
    return type.equals(column.type);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(data);
    result = 31 * result + type.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return getSimpleTypeName() + " Column" +
        "\n" + Arrays.toString(data);
  }
}
