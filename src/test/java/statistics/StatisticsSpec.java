package statistics;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public final class StatisticsSpec {
	
	public static final double TOL = 1E-4;
	
	@Test
	public void whenSumComputedThenResultCorrect() {
		double[] data = {3.5, 4.9, 9.1};
		double expected = 17.5;
		double sum = Statistics.sumOf(data);
		assertThat(sum, is(equalTo(expected)));
	}
	
	
	@Test
	public void whenMeanComputedThenResultCorrect() {
		double[] data = {3.0, 10.5, 1.5};
		double expected = 5.0;
		double mean = Statistics.meanOf(data);
		assertThat(mean, is(equalTo(expected)));
	}
	
	@Test
	public void whenVarianceComputedThenResultCorrect() {
		double[] data = {3.5, 7.0, 11.5};
		double expected = 16.08333;
		double variance = Statistics.varianceOf(data);
		assertThat(variance, is(closeTo(expected, TOL)));
	}
	
	@Test
	public void whenSumOfSquaresComputedThenResultCorrect() {
		double[] data = {3.5, 7.0, 11.5};
		double expected = 193.5;
		double sumOfSquares = Statistics.sumOfSquared(data);
		assertThat(sumOfSquares, is(equalTo(expected)));
	}
	
	@Test
	public void whenDifferencesTakenThenResultCorrect() {
		double[] data = {3.0, 9.0};
		double point = 6.0;
		double[] expected = new double[] {-3.0, 3.0};
		double[] diffs = Statistics.differences(data, point);
		assertThat(diffs, is(equalTo(expected)));
	}
	
	@Test
	public void whenSquaresTakenThenResultCorrect() {
		double[] data = {3.0, 9.0};
		double[] expected = {9.0, 81.0};
		double[] squared = Statistics.squared(data);
		assertThat(squared, is(equalTo(expected)));
	}
	
	@Test
	public void whenSumOfSquaredDifferencesComputedThenResultCorrect() {
		double[] data = {3.0, 9.0};
		double point = 6.0;
		double expected = 18.0;
		double sumOfSquaredDiffs = Statistics.sumOfSquaredDifferences(data, point);
		assertThat(sumOfSquaredDiffs, is(equalTo(expected)));
	}
	
	@Test
	public void whenStdDeviationComputedThenResultCorrect() {
		double[] data = {3.5, 7.0, 11.5};
		double expected = 4.010403;
		double stdDeviation = Statistics.stdDeviationOf(data);
		assertThat(stdDeviation, is(closeTo(expected, TOL)));
	}
	
	@Test
	public void whenCovarianceComputedThenResultCorrect() {
		double[] data = {3.5, 7.0, 11.5};
		double[] data2 = {3.0, 1.4, 10.0};
		double expected = 14.85;
		assertThat(Statistics.covariance(data, data2), is(closeTo(expected, TOL)));
	}

}
