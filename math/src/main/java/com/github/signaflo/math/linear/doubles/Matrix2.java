package com.github.signaflo.math.linear.doubles;

import java.util.ArrayList;
import java.util.List;

/**
 * A real-valued matrix.
 *
 * @author Jacob Rachiele
 * Aug. 02, 2017
 */
interface Matrix2 {

    /**
     * Get a new row builder. The builder's initial storage capacity will be
     * set to the default value.
     *
     * @return a new row builder.
     */
    static RowBuilder rowBuilder() {
        return new RowBuilder();
    }

    /**
     * Get a new column builder. The builder's initial storage capacity will be
     * set to the default value.
     *
     * @return a new column builder.
     */
    static ColumnBuilder columnBuilder() {
        return new ColumnBuilder();
    }

    /**
     * Get a new row builder. The builder's initial storage capacity will be
     * set to the value provided by nrow.
     *
     * @return a new row builder.
     */
    static RowBuilder rowBuilder(int nrow) {
        return new RowBuilder(nrow);
    }

    /**
     * Get a new column builder. The builder's initial storage capacity will be
     * set to the value provided by ncol.
     *
     * @return a new column builder.
     */
    static ColumnBuilder columnBuilder(int ncol) {
        return new ColumnBuilder(ncol);
    }

    /**
     * Create a new matrix from the given list of row vectors.
     *
     * @param rowVectors the row vectors constituting the matrix.
     * @return a new matrix from the given list of row vectors.
     */
    static Matrix2 fromRows(List<Vector> rowVectors) {
        return new RowMatrix(rowVectors);
    }

    static Matrix2 fromColumns(List<Vector> columnVectors) {
        return new ColumnMatrix(columnVectors);
    }

    static Matrix2 fromRows(double[]... rowVectors) {
        List<Vector> rows = new ArrayList<>(rowVectors.length);
        for (int i = 0; i < rowVectors.length; i++) {
            rows.add(Vector.from(rowVectors[i]));
        }
        return new RowMatrix(rows);
    }

    static Matrix2 fromColumns(double[]... columnVectors) {
        List<Vector> columns = new ArrayList<>(columnVectors.length);
        for (int i = 0; i < columnVectors.length; i++) {
            columns.add(Vector.from(columnVectors[i]));
        }
        return new ColumnMatrix(columns);
    }

    List<Vector> getRows();

    List<Vector> getColumns();

    int nrow();

    int ncol();

    Matrix2 times(Matrix2 other);

    Vector times(Vector vector);

    class RowBuilder {

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

        Matrix2 build() {
            return new RowMatrix(rows);
        }
    }

    class ColumnBuilder {

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

        Matrix2 build() {
            return new ColumnMatrix(this.columns);
        }
    }
}
