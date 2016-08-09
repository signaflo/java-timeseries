package timeseries;

import org.junit.Test;

import timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertArrayEquals;

import java.time.ZonedDateTime;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class TimeSeriesSpec {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void whenTimeSeriesMeanTakenThenResultCorrect() {
		double[] data = new double[] {3.0, 7.0, 5.0};
		TimeSeries series = new TimeSeries(ZonedDateTime.now(), data);
		assertThat(series.mean(), is(equalTo(5.0)));
	}
	
	@Test
	public void whenAutoCovarianceComputedTheResultIsCorrect() {
		TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
		double[] acvf = new double[] {4.889, -1.837, -0.407,  1.310, -1.917, 0.406};
		for (int i = 0; i < acvf.length; i++) {
			assertThat(series.autoCovarianceAtLag(i), is(closeTo(acvf[i], 1E-2)));
		}
	}
	
	@Test
	public void whenAutoCorrelationComputedTheResultIsCorrect() {
		TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
		double[] acf = new double[] {1.000, -0.376, -0.083,  0.268, -0.392,  0.083};
		for (int i = 0; i < acf.length; i++) {
			assertThat(series.autoCorrelationAtLag(i), is(closeTo(acf[i], 1E-2)));
		}
	}

	@Test
	public void whenAutoCovarianceComputedUpToLagKThenResultingArrayCorrect() {
		TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
		double[] expected = new double[] {4.889, -1.837, -0.407,  1.310, -1.917, 0.406};
		double[] result = series.autoCovarianceUpToLag(9);
		assertArrayEquals(expected, result, 1E-2);
	}
	
	@Test
	public void whenAutoCorrelationComputedUpToLagKThenResultingArrayCorrect() {
		TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
		double[] expected = new double[] {1.000, -0.376, -0.083,  0.268, -0.392,  0.083 };
		double[] result = series.autoCorrelationUpToLag(5);
		assertArrayEquals(expected, result, 1E-2);
	}
}
