/*
 * Copyright (c) 2017 Jacob Rachiele
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

package math.linear.doubles;

import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public final class RowMatrix {

    enum VectorType {
        ROW,
        COLUMN
    }

    private final List<Vector> rowVectors;
    private final int nrow;
    private final int ncol;

    static RowMatrix fromRowVectors(List<Vector> rowVectors) {
        return new RowMatrix(rowVectors);
    }

    static RowMatrix fromColumnVectors(List<Vector> columnVectors) {
        return new RowMatrix(VectorType.COLUMN, columnVectors);
    }

    RowMatrix(@NonNull List<Vector> rowVectors) {
        this(VectorType.ROW, rowVectors);
    }

    RowMatrix(@NonNull VectorType vectorType, @NonNull List<Vector> vectors) {
        this.rowVectors = getRowVectorsByType(vectorType, vectors);
        this.nrow = this.rowVectors.size();
        if (this.nrow > 0) {
            this.ncol = rowVectors.get(0).size();
        } else {
            // allow creation of a 0x0 Matrix.
            this.ncol = 0;
        }
    }

    RowMatrix(int nrow, int ncol) {
        rowVectors = new ArrayList<>(nrow);
        this.nrow = nrow;
        this.ncol = ncol;
    }

    private List<Vector> getRowVectorsByType(VectorType vectorType, List<Vector> vectors) {
        switch (vectorType) {
            case ROW: default:
                return vectors;
            case COLUMN:
                return columnsToRows(vectors);
        }
    }

    public List<Vector> getRows() {
        return new ArrayList<>(rowVectors);
    }

    public List<Vector> getColumns() {
        List<Vector> columns = new ArrayList<>(this.ncol);
        if (this.rowVectors.isEmpty()) {
            return columns;
        }
        double[] columnElements = new double[this.nrow];
        for (int j = 0; j < this.ncol; j++) {
            for (int i = 0; i < this.nrow; i++) {
                columnElements[i] = this.rowVectors.get(i).at(j);
            }
            columns.add(Vector.from(columnElements));
        }
        return columns;
    }

    public int nrow() {
        return this.nrow;
    }

    public int ncol() {
        return this.ncol;
    }

    RowMatrix times(RowMatrix other) {
        if (this.ncol != other.nrow) {
            throw new IllegalArgumentException("The number of columns of this matrix must equal the " +
                                               "number of rows of the other matrix.");
        }
        List<Vector> otherColumns = other.getColumns();
        List<Vector> newRows = new ArrayList<>(this.nrow);
        for (int i = 0; i < this.nrow; i++) {
            double[] newRowElements = new double[other.ncol];
            for (int j = 0; j < other.ncol; j++) {
                newRowElements[j] = this.rowVectors.get(i).dotProduct(otherColumns.get(j));
            }
            newRows.add(Vector.from(newRowElements));
        }
        return new RowMatrix(newRows);
    }

    Vector times(Vector vector) {
        if (vector.size() != this.ncol) {
            throw new IllegalArgumentException("The number of elements of the given vector must equal " +
                                               "the number of columns of this matrix.");
        }
        double[] newVectorElements = new double[this.nrow];
        for (int i = 0; i < this.nrow; i++) {
            newVectorElements[i] = rowVectors.get(i).dotProduct(vector);
        }
        return Vector.from(newVectorElements);
    }

    private List<Vector> columnsToRows(List<Vector> columnVectors) {
        final int nrow = columnVectors.get(0).size();
        final int ncol = columnVectors.size();
        List<Vector> rowVectors = new ArrayList<>(nrow);
        for (int i = 0; i < nrow; i++) {
            double[] rowElements = new double[ncol];
            for (int j = 0; j < ncol; j++) {
                rowElements[j] = columnVectors.get(j).at(i);
            }
            rowVectors.add(Vector.from(rowElements));
        }
        return rowVectors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowMatrix rowMatrix = (RowMatrix) o;

        if (nrow != rowMatrix.nrow) return false;
        if (ncol != rowMatrix.ncol) return false;
        if (!rowVectors.equals(rowMatrix.rowVectors)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rowVectors.hashCode();
        result = 31 * result + nrow;
        result = 31 * result + ncol;
        return result;
    }

    public static RowBuilder rowBuilder(int nrow) {
        return new RowBuilder(nrow);
    }

    public static ColumnBuilder columnBuilder(int ncol) {
        return new ColumnBuilder(ncol);
    }

    public static class RowBuilder {

        private List<Vector> rows;

        private RowBuilder() {
            this.rows = new ArrayList<>();
        }

        private RowBuilder(final int nrow) {
            this.rows = new ArrayList<>(nrow);
        }

        RowBuilder addRow(Vector vector) {
            this.rows.add(vector);
            return this;
        }

        RowBuilder addRow(double... vector) {
            this.rows.add(Vector.from(vector));
            return this;
        }

        RowBuilder setRow(int i, double... vector) {
            this.rows.set(i, Vector.from(vector));
            return this;
        }

        RowBuilder setRow(int i, Vector vector) {
            this.rows.set(i, vector);
            return this;
        }

        RowMatrix build() {
            return new RowMatrix(rows);
        }
    }

    public static class ColumnBuilder {

        private List<Vector> columns;

        private ColumnBuilder() {
            this.columns = new ArrayList<>();
        }

        private ColumnBuilder(final int ncol) {
            this.columns = new ArrayList<>(ncol);
        }

        ColumnBuilder addColumn(Vector vector) {
            this.columns.add(vector);
            return this;
        }

        ColumnBuilder setColumn(int j, Vector vector) {
            this.columns.set(j, vector);
            return this;
        }

        ColumnBuilder addColumn(double... vector) {
            this.columns.add(Vector.from(vector));
            return this;
        }

        ColumnBuilder setColumn(int j, double... vector) {
            this.columns.set(j, Vector.from(vector));
            return this;
        }

        RowMatrix build() {
            return new RowMatrix(VectorType.COLUMN, this.columns);
        }
    }
}
