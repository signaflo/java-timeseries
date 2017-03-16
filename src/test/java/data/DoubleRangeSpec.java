package data;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

public class DoubleRangeSpec {

    @Test
    public void whenDoubleRangeInclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
        assertArrayEquals(expected, DoubleRange.inclusiveRange(from, to).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeExclusiveCreatedCorrectObjectReturned() {
        int from = 0;
        int to = 5;
        double[] expected = new double[]{0.0, 1.0, 2.0, 3.0, 4.0};
        assertArrayEquals(expected, DoubleRange.exclusiveRange(from, to).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeInclusiveCreatedCorrectObjectReturnedAgain() {
        int from = 2;
        int to = 7;
        double[] expected = new double[]{2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
        assertArrayEquals(expected, DoubleRange.inclusiveRange(from, to).asArray(), 1E-8);
    }

    @Test
    public void whenDoubleRangeExclusiveCreatedCorrectObjectReturnedAgain() {
        int from = 2;
        int to = 5;
        double[] expected = new double[]{2.0, 3.0, 4.0};
        assertArrayEquals(expected, DoubleRange.exclusiveRange(from, to).asArray(), 1E-8);
    }

    @Test
    public void whenSumComputedResultCorrect() {
        DoubleRange range = DoubleRange.exclusiveRange(2, 5);
        assertThat(range.sum(), is(closeTo(9.0, 1E-8)));
    }

    @Test
    public void whenAsListThenCorrectListReturned() {
        List<Double> expected = Arrays.asList(1.0, 2.0, 3.0);
        DoubleRange range = DoubleRange.inclusiveRange(1, 3);
        assertThat(range.asList(), is(expected));

    }
}
