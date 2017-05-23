package data;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

public class RangeSpec {

    @Test
    public void whenIntRangeInclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0, 1, 2, 3, 4, 5};
        assertArrayEquals(expected, Range.inclusiveRange(from, to, 1.0).asArray(), 0.0);
    }

    @Test
    public void whenIntRangeExclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0, 1, 2, 3, 4};
        assertArrayEquals(expected, Range.exclusiveRange(from, to, 1.0).asArray(), 0.0);
    }

    @Test
    public void whenIntRangeInclusiveCreatedCorrectObjectReturnedTestTwo() {
        int from = 2;
        int to = 7;
        double[] expected = new double[]{2, 3, 4, 5, 6, 7};
        assertArrayEquals(expected, Range.inclusiveRange(from, to, 1.0).asArray(), 0.0);
    }

    @Test
    public void whenIntRangeExclusiveCreatedCorrectObjectReturnedTestTwo() {
        int from = 2;
        int to = 5;
        double[] expected = new double[]{2, 3, 4};
        assertArrayEquals(expected, Range.exclusiveRange(from, to, 1.0).asArray(), 0.0);
    }

    @Test
    public void whenSumComputedResultCorrect() {
        Range range = Range.inclusiveRange(2, 5, 1.0);
        assertThat(range.sum(), is(equalTo(14.0)));
    }

    @Test
    public void whenSliceTakenResultingArrayCorrect() {
        Range range = Range.exclusiveRange(3, 10, 1.0);
        double[] expected = new double[]{5, 6, 7};
        assertArrayEquals(expected, DoubleFunctions.slice(range.asArray(), 2, 5), 0.0);
    }

    @Test
    public void whenInclusiveSliceTakenResultingArrayCorrect() {
        Range range = Range.exclusiveRange(3, 10, 1.0);
        double[] expected = new double[]{5, 6, 7, 8};
        assertArrayEquals(expected, DoubleFunctions.slice(range.asArray(), 2, 6), 0.0);
    }

    @Test
    public void whenDoubleRangeInclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
        assertArrayEquals(expected, Range.inclusiveRange(from, to, 1.0).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeExclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0.0, 1.0, 2.0, 3.0, 4.0};
        assertArrayEquals(expected, Range.exclusiveRange(from, to, 1.0).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeInclusiveCreatedCorrectObjectReturnedAgain() {
        int from = 2;
        int to = 7;
        double[] expected = new double[]{2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
        assertArrayEquals(expected, Range.inclusiveRange(from, to, 1.0).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeExclusiveCreatedCorrectObjectReturnedAgain() {
        int from = 2;
        int to = 5;
        double[] expected = new double[]{2.0, 3.0, 4.0};
        assertArrayEquals(expected, Range.exclusiveRange(from, to, 1.0).asArray(), 1E-8);
    }

    @Test
    public void whenAsListThenCorrectListReturned() {
        List<Double> expected = Arrays.asList(1.0, 2.0, 3.0);
        Range range = Range.inclusiveRange(1, 3, 1.0);
        assertThat(range.asList(), is(expected));

    }

}
