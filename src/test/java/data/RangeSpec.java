package data;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeSpec {

	@Test
	public void whenIntRangeInclusiveCreatedCorrectObjectReturned() {
		int from = 0;
		int to = 5;
		int[] expected = new int[] {0, 1, 2, 3, 4, 5};
		assertArrayEquals(expected, Range.newRangeInclusive(from, to).asArray());
	}
	
	@Test
	public void whenIntRangeExclusiveCreatedCorrectObjectReturned() {
		int from = 0;
		int to = 5;
		int[] expected = new int[] {0, 1, 2, 3, 4};
		assertArrayEquals(expected, Range.newRangeExclusive(from, to).asArray());
	}
	
	@Test
	public void whenIntRangeInclusiveCreatedCorrectObjectReturnedTestTwo() {
		int from = 2;
		int to = 7;
		int[] expected = new int[] {2, 3, 4, 5, 6, 7};
		assertArrayEquals(expected, Range.newRangeInclusive(from, to).asArray());
	}
	
	@Test
	public void whenIntRangeExclusiveCreatedCorrectObjectReturnedTestTwo() {
		int from = 2;
		int to = 5;
		int[] expected = new int[] {2, 3, 4};
		assertArrayEquals(expected, Range.newRangeExclusive(from, to).asArray());
	}

}
