package math.linear.doubles;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Aug. 03, 2017
 */
@EqualsAndHashCode @ToString
class MutableMatrix {

    private final int nrow;
    private final int ncol;
    private final double[] data;
    private final int[] rowIndices;

    MutableMatrix(int nrow, int ncol, double... data) {
        this.nrow = nrow;
        this.ncol = ncol;
        this.data = data.clone();
        this.rowIndices = new int[nrow];
        for (int i = 0; i < this.nrow; i++) {
            this.rowIndices[i] = i * ncol;
        }
    }

    double get(int i, int j) {
        return this.data[this.rowIndices[i] + j];
    }

    double[] getRow(int i) {
        double[] row = new double[this.ncol];
        System.arraycopy(this.data, this.rowIndices[i], row, 0, row.length);
        return row;
    }

    double[] getColumn(int j) {
        double[] column = new double[this.nrow];
        for (int i = 0; i < this.nrow; i++) {
            column[i] = this.data[this.rowIndices[i] + j];
        }
        return column;
    }

    // First elementary row operation.
    void swapRows(int i, int j) {
        int temp = this.rowIndices[i];
        this.rowIndices[i] = this.rowIndices[j];
        this.rowIndices[j] = temp;
    }

    // Second elementary row operation.
    void scaleRow(int i, double alpha) {
        for (int j = 0; j < ncol; j++) {
            this.data[rowIndices[i] + j] *= alpha;
        }
    }

    double[] scaleAndReturnRow(int i, double alpha) {
        double[] row = new double[this.ncol];
        for (int j = 0; j < ncol; j++) {
            row[j] = this.data[rowIndices[i] + j] * alpha;
        }
        return row;
    }

    void addRow(double[] row, int i) {
        for (int j = 0; j < this.ncol; j++) {
            this.data[rowIndices[i] + j] += row[j];
        }
    }

    void scaleAndAddRow(int i, int j, double alpha) {
        double[] scaledRow = scaleAndReturnRow(i, alpha);
        addRow(scaledRow, j);
    }
}
