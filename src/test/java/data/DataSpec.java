package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public class DataSpec {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	@SuppressWarnings("unused")
	public void whenDataSetCreatedWithNullArrayThenExceptionThrown() {
		double[] data = null;
		exception.expect(IllegalArgumentException.class);
		Data dataSet = new Data(data);
	}
	
	@Test
	public void whenSumRequestedThenResultCorrect() {
		double[] data = new double[] {3.0, 7.0, 10.0};
		Data dataSet = new Data(data);
		double expected = 20.0;
		double actual = dataSet.sum();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenMeanRequestedThenResultCorret() {
		double[] data = new double[] {3.0, 5.5, 6.5};
		Data dataSet = new Data(data);
		double expected = 5.0;
		double actual = dataSet.mean();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenLengthRequestedThenResultCorrect() {
		double[] data = new double[] {5.0, 7.5};
		Data dataSet = new Data(data);
		int expected = 2;
		int actual = dataSet.n();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenTimesOperationCalledThenResultCorrect() {
		double[] data1 = new double[] {5.0, 7.5};
		double[] data2 = new double[] {3.0, 10.0};
		Data dataSet1 = new Data(data1);
		Data dataSet2 = new Data(data2);
		double[] expected = new double[] {15.0, 75.0};
		assertThat(dataSet1.times(dataSet2), is(equalTo(expected)));
	}
	
	@Test
	public void whenPlusOperationCalledThenResultCorrect() {
		double[] data1 = new double[] {5.0, 7.5};
		double[] data2 = new double[] {3.0, 10.0};
		Data dataSet1 = new Data(data1);
		Data dataSet2 = new Data(data2);
		double[] expected = new double[] {8.0, 17.5};
		assertThat(dataSet1.plus(dataSet2), is(equalTo(expected)));
	}

}
