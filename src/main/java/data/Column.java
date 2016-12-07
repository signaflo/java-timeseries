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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jacob Rachiele
 * Date: Dec 07 2016
 *
 * An immutable and thread-safe column of data. Objects of this class typically compose a {@link DataFrame}, but may
 * exist seperately. A column consists of the data itself together with a {@link Class} variable that acts as
 * metadata indicating the data's type. This is done to allow dataframes to hold columns of differing types.
 */
public final class Column<T> {

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
