package timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

import java.time.OffsetDateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import data.TestData;

public class TimeSeriesSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenBoxCoxTransformationWithOutOfRangeLambdaExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    TimeSeries series = TestData.ausbeerSeries();
    series.transform(2.5);
  }

  @Test
  public void whenBoxCoxTransformLogThenDataTransformedCorrectly() {
    double[] data = new double[] { 3.0, 7.0, Math.E };
    double[] expected = new double[] { Math.log(3.0), Math.log(7.0), 1.0 };
    TimeSeries timeSeries = new TimeSeries(data);
    assertArrayEquals(expected, timeSeries.transform(0).series(), 1E-4);
  }

  @Test
  public void whenBoxCoxInvTransformLogThenDataTransformedCorrectly() {
    double[] data = new double[] { Math.log(3.0), Math.log(7.0), 1.0 };
    double[] expected = new double[] { 3.0, 7.0, Math.E };
    TimeSeries timeSeries = new TimeSeries(data);
    assertArrayEquals(expected, timeSeries.backTransform(0).series(), 1E-4);
  }

  @Test
  public void whenTimeSeriesMeanTakenThenResultCorrect() {
    double[] data = new double[] { 3.0, 7.0, 5.0 };
    TimeSeries series = new TimeSeries(OffsetDateTime.now(), data);
    assertThat(series.mean(), is(equalTo(5.0)));
  }

  @Test
  public void whenAutoCovarianceComputedTheResultIsCorrect() {
    TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
    double[] acvf = new double[] { 4.889, -1.837, -0.407, 1.310, -1.917, 0.406 };
    for (int i = 0; i < acvf.length; i++) {
      assertThat(series.autoCovarianceAtLag(i), is(closeTo(acvf[i], 1E-2)));
    }
  }

  @Test
  public void whenAutoCorrelationComputedTheResultIsCorrect() {
    TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
    double[] acf = new double[] { 1.000, -0.376, -0.083, 0.268, -0.392, 0.083 };
    for (int i = 0; i < acf.length; i++) {
      assertThat(series.autoCorrelationAtLag(i), is(closeTo(acf[i], 1E-2)));
    }
  }

  @Test
  public void whenAutoCovarianceComputedUpToLagKThenResultingArrayCorrect() {
    TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
    double[] expected = new double[] { 4.889, -1.837, -0.407, 1.310, -1.917, 0.406 };
    double[] result = series.autoCovarianceUpToLag(9);
    assertArrayEquals(expected, result, 1E-2);
  }

  @Test
  public void whenAutoCorrelationComputedUpToLagKThenResultingArrayCorrect() {
    TimeSeries series = new TimeSeries(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
    double[] expected = new double[] { 1.000, -0.376, -0.083, 0.268, -0.392, 0.083 };
    double[] result = series.autoCorrelationUpToLag(5);
    assertArrayEquals(expected, result, 1E-2);
  }

  @Test
  public void whenFivePeriodMovingAverageComputedResultCorrect() {
    TimeSeries series = TestData.elecSales();
    double[] expected = new double[] { 2381.53, 2424.556, 2463.758, 2552.598, 2627.7, 2750.622, 2858.348, 3014.704,
        3077.3, 3144.52, 3188.7, 3202.32, 3216.94, 3307.296, 3398.754, 3485.434 };
    double[] result = series.movingAverage(5).series();
    assertArrayEquals(expected, result, 1E-2);
    System.out.println(series.movingAverage(5));
  }
}
