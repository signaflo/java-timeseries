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
import java.util.List;

public final class Schema {

  private final List<DataType> schema;

  public Schema(final List<DataType> schema) {
    this.schema = schema;
  }

  public Schema(final int numColumns) {
    this.schema = new ArrayList<>(numColumns);
  }

  public Schema() {
    this.schema = new ArrayList<>();
  }

  public final void add(DataType type) {
    this.schema.add(type);
  }

  public final DataType remove(final int i) {
    return this.schema.remove(i);
  }

  public final boolean remove(final DataType type) {
    return this.schema.remove(type);
  }

  public final DataType get(final int i) {
    return this.schema.get(i);
  }

  public final int size() {
    return this.schema.size();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Schema schema1 = (Schema) o;

    return schema.equals(schema1.schema);
  }

  @Override
  public int hashCode() {
    return schema.hashCode();
  }

  @Override
  public String toString() {
    return schema.toString();
  }
}
