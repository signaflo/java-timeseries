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

import java.util.Collections;
import java.util.List;

/**
 * An immutable version of a {@link DataFrame}.
 *
 * @author Jacob Rachiele
 * Date: Dec 07 2016
 *
 */
public class FixedDataFrame {

  private final List<Column<?>> data;

  /**
   * Create a new, empty fixed dataframe.
   */
  public FixedDataFrame() {
    this.data = Collections.unmodifiableList(Collections.emptyList());
  }

  /**
   * Create a new fixed dataframe filled with the given columns.
   * @param data the columns to fill the dataframe with.
   */
  public FixedDataFrame(final List<Column<?>> data) {
    this.data = Collections.unmodifiableList(data);
  }

  /**
   * Get the ith column of this dataframe.
   * @param i the index of the column to retrieve;
   * @return the ith column of this dataframe.
   */
  public Column<?> getColumn(final int i) {
    return this.data.get(i);
  }

  /**
   * Get the ith column of this dataframe as a column of type Double.
   * @param i the index of the column to retrieve;
   * @return the ith column of this dataframe as a column of type Double.
   */
  public Column<Double> getColumnAsDouble(final int i) {
    return this.data.get(i).asDouble();
  }

  /**
   * Get the ith column of this dataframe as a column of type String.
   * @param i the index of the column to retrieve;
   * @return the ith column of this dataframe as a column of type String.
   */
  public Column<String> getColumnAsString(final int i) {
    return this.data.get(i).asString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FixedDataFrame that = (FixedDataFrame) o;

    return data.equals(that.data);
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    final int nrows = (data.size() > 0)? data.get(0).size() : 0;
    final int fixedWidth = 15;
    Column<?> columnData;
    String s;
    for (int i = 0; i < data.size(); i++) {
      s = "Type:" + data.get(i).getSimpleTypeName();
      sb.append(String.format("%-" + fixedWidth + "." + fixedWidth + "s", s));
      sb.append("|");
    }
    sb.append("\n");
    for (int j = 0; j < nrows; j++) {
      for (int i = 0; i < data.size(); i++) {
        columnData = data.get(i);
        sb.append(String.format("%-" + fixedWidth + "." + fixedWidth + "s", columnData.get(j)));
        sb.append("|");
      }
      if (j != nrows - 1) {
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  /**
   * Print the string representation of this dataframe to the console.
   */
  public void print() {
    System.out.println(toString());
  }
}
