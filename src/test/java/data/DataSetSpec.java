package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

public class DataSetSpec {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	@SuppressWarnings("unused")
	public void whenDataSetCreatedWithNullArrayThenExceptionThrown() {
		double[] data = null;
		exception.expect(IllegalArgumentException.class);
		DataSet dataSet = new DataSet(data);
	}
	
	@Test
	public void whenSumRequestedThenResultCorrect() {
		double[] data = new double[] {3.0, 7.0, 10.0};
		DataSet dataSet = new DataSet(data);
		double expected = 20.0;
		double actual = dataSet.sum();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenMeanRequestedThenResultCorrect() {
		double[] data = new double[] {3.0, 5.5, 6.5};
		DataSet dataSet = new DataSet(data);
		double expected = 5.0;
		double actual = dataSet.mean();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenLengthRequestedThenResultCorrect() {
		double[] data = new double[] {5.0, 7.5};
		DataSet dataSet = new DataSet(data);
		int expected = 2;
		int actual = dataSet.n();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenTimesOperationCalledThenResultCorrect() {
		double[] data1 = new double[] {5.0, 7.5};
		double[] data2 = new double[] {3.0, 10.0};
		DataSet dataSet1 = new DataSet(data1);
		DataSet dataSet2 = new DataSet(data2);
		double[] expected = new double[] {15.0, 75.0};
		assertThat(dataSet1.times(dataSet2).data(), is(equalTo(expected)));
	}
	
	@Test
	public void whenPlusOperationCalledThenResultCorrect() {
		double[] data1 = new double[] {5.0, 7.5};
		double[] data2 = new double[] {3.0, 10.0};
		DataSet dataSet1 = new DataSet(data1);
		DataSet dataSet2 = new DataSet(data2);
		double[] expected = new double[] {8.0, 17.5};
		assertThat(dataSet1.plus(dataSet2).data(), is(equalTo(expected)));
	}

}
