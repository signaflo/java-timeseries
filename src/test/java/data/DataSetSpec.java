package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.closeTo;

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
	public void whenMedianRequestedThenResultCorrect() {
		double[] data = new double[] {5.5, 6.5, 3.0};
		DataSet dataSet = new DataSet(data);
		double expected = 5.5;
		double actual = dataSet.median();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenMedianRequestedEvenNumDataPointsThenResultCorrect() {
		double[] data = new double[] {6.5, 10.0, 3.0, 5.5};
		DataSet dataSet = new DataSet(data);
		double expected = 6.0;
		double actual = dataSet.median();
		assertThat(actual, is(equalTo(expected)));
	}
	
	@Test
	public void whenVarianceRequestedEvenNumDataPointsThenResultCorrect() {
		double[] data = new double[] {6.5, 10.0, 3.0, 5.5};
		DataSet dataSet = new DataSet(data);
		double expected = 8.416667;
		double actual = dataSet.variance();
		assertThat(actual, is(closeTo(expected, 1E-4)));
	}
	
	@Test
	public void whenStdDeviationRequestedEvenNumDataPointsThenResultCorrect() {
		double[] data = new double[] {6.5, 10.0, 3.0, 5.5};
		DataSet dataSet = new DataSet(data);
		double expected = 2.901149;
		double actual = dataSet.stdDeviation();
		assertThat(actual, is(closeTo(expected, 1E-4)));
	}
	
	@Test
	public void whenCovarianceRequestedEvenNumDataPointsThenResultCorrect() {
		double[] data1 = new double[] {6.5, 10.0, 3.0, 5.5};
		double[] data2 = new double[] {3.0, 5.0, 7.0, 4.5};
		DataSet dataSet = new DataSet(data1);
		DataSet dataSet2 = new DataSet(data2);
		double expected =  -2.208333;
		double actual = dataSet.covariance(dataSet2);
		assertThat(actual, is(closeTo(expected, 1E-4)));
	}
	
	@Test
	public void whenCorrelationRequestedEvenNumDataPointsThenResultCorrect() {
		double[] data1 = new double[] {6.5, 10.0, 3.0, 5.5};
		double[] data2 = new double[] {3.0, 5.0, 7.0, 4.5};
		DataSet dataSet = new DataSet(data1);
		DataSet dataSet2 = new DataSet(data2);
		double expected = -0.4607651;
		double actual = dataSet.correlation(dataSet2);
		assertThat(actual, is(closeTo(expected, 1E-4)));
	}
	
	@Test
	public void whenLengthRequestedThenResultCorrect() {
		double[] data = new double[] {5.0, 7.5};
		DataSet dataSet = new DataSet(data);
		long expected = 2;
		long actual = dataSet.n();
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
		DataSet dataSet1 = new DataSet(5.0, 7.5);
		DataSet dataSet2 = new DataSet(3.0, 10.0);
		double[] expected = new double[] {8.0, 17.5};
		assertThat(dataSet1.plus(dataSet2).data(), is(equalTo(expected)));
	}

}
