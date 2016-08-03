package data;

import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleRangeSpec {
	
	@Test
	public void whenDoubleRangeInclusiveCreatedCorrectObjectReturned() {
		int from = 0;
		int to = 5;
		double[] expected = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
		assertArrayEquals(expected, DoubleRange.newRangeInclusive(from, to).asArray(), 1E-8);
	}
	
	@Test
	public void whenDoubleRangeExclusiveCreatedCorrectObjectReturned() {
		int from = 0;
		int to = 5;
		double[] expected = new double[] {0.0, 1.0, 2.0, 3.0, 4.0};
		assertArrayEquals(expected, DoubleRange.newRangeExclusive(from, to).asArray(), 1E-8);
	}
	
	@Test
	public void whenDoubleRangeInclusiveCreatedCorrectObjectReturnedTestTwo() {
		int from = 2;
		int to = 7;
		double[] expected = new double[] {2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		assertArrayEquals(expected, DoubleRange.newRangeInclusive(from, to).asArray(), 1E-8);
	}
	
	@Test
	public void whenDoubleRangeExclusiveCreatedCorrectObjectReturnedTestTwo() {
		int from = 2;
		int to = 5;
		double[] expected = new double[] {2.0, 3.0, 4.0};
		assertArrayEquals(expected, DoubleRange.newRangeExclusive(from, to).asArray(), 1E-8);
	}


}
