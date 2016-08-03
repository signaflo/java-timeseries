package data;

import static org.junit.Assert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

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
	
	@Test
	public void whenSumComputedResultCorrect() {
		Range range = Range.newRangeInclusive(2,  5);
		assertThat(range.sum(), is(equalTo(14.0)));
	}
	
	@Test
	public void whenSliceTakenResultingArrayCorrect() {
		Range range = Range.newRangeExclusive(3, 10);
		int[] expected = new int[] {5, 6, 7};
		assertThat(range.sliceExclusive(2, 5).asArray(), is(equalTo(expected)));
	}
	
	@Test
	public void whenInclusiveSliceTakenResultingArrayCorrect() {
		Range range = Range.newRangeExclusive(3, 10);
		int[] expected = new int[] {5, 6, 7, 8};
		assertThat(range.sliceInclusive(2, 5).asArray(), is(equalTo(expected)));
	}

}
