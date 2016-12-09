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
 * TODO: Describe what this class is.
 */
public class DataFrame {

  private final List<Column<?>> data;

  public DataFrame() {
    data = new ArrayList<>();
  }

  public DataFrame(final List<Column<?>> data) {
    this.data = data;
  }

  public void add(final Column<?> columnData) {
    this.data.add(columnData);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DataFrame dataFrame = (DataFrame) o;

    return data.equals(dataFrame.data);
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    final int nrows = (data.size() > 0)? data.get(0).size() : 0;
    Column<?> columnData;
    String s;
    int stringLength;
    sb.append("Type ||");
    for (int i = 0; i < data.size(); i++) {
      columnData = data.get(i);
      s = data.get(i).getSimpleTypeName();
      stringLength = s.length();
      sb.append(String.format("%-" + stringLength + "s", columnData.getSimpleTypeName()));
      sb.append("|");
    }
    sb.append("\n");
    for (int j = 0; j < nrows; j++) {
      sb.append("Value||");
      for (int i = 0; i < data.size(); i++) {
        columnData = data.get(i);
        s = data.get(i).getSimpleTypeName();
        stringLength = s.length();
        sb.append(String.format("%-" + stringLength + "s", columnData.get(j)));
        sb.append("|");
      }
      if (j != nrows - 1) {
        sb.append("\n");
      }
    }
    return sb.toString();
  }
}
