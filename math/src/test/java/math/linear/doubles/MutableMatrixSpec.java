package math.linear.doubles;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Aug. 03, 2017
 */
public class MutableMatrixSpec {

    // 4.0 2.0
    // 1.5 2.5
    // 1.0 3.0
    private MutableMatrix A = new MutableMatrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);

    @Test
    public void whenGetRowIColumnJThenCorrectValue() {
        assertThat(A.get(0, 1), is(2.0));
    }

    @Test
    public void whenSwapRowsThenGetRowIColumnJCorrectAfterSwapping() {
        A.swapRows(0, 2);
        assertThat(A.get(0, 1), is(3.0));
    }

    @Test
    public void whenGetRowAfterSwapThenCorrectRow() {
        A.swapRows(0, 2);
        assertThat(A.getRow(2), is(new double[] {4.0, 2.0}));
    }

    @Test
    public void whenGetColumnAfterSwapThenCorrectColumn() {
        A.swapRows(0, 2);
        assertThat(A.getColumn(1), is(new double[] {3.0, 2.5, 2.0}));
    }

    @Test
    public void whenScaleRowThenRowMultipliedByAlpha() {
        double alpha = 2.0;
        A.scaleRow(1, alpha);
        assertThat(A.getRow(1), is(new double[] {3.0, 5.0}));
    }

    @Test
    public void whenScaleAndAddRowThenCorrectChange() {
        double alpha = 2.0;
        A.scaleAndAddRow(1, 2, alpha);
        assertThat(A.getRow(2), is(new double[] {4.0, 8.0}));
    }

    @Test
    public void whenScaleAndAddRowThenFirstRowUnchanged() {
        double alpha = 2.0;
        A.scaleAndAddRow(1, 2, alpha);
        assertThat(A.getRow(1), is(new double[] {1.5, 2.5}));
    }

}