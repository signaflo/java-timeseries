package timeseries;

import java.time.Instant;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import data.DataSet;

/**
 * A sequence of observations taken at regular time intervals.
 * @author Jacob Rachiele
 *
 */
public final class TimeSeries extends DataSet {
	
	private final int n;
	private final double mean;
	private final double[] series;
	private final double[] timeIndices;
	private String name = "Time Series";
	private final TemporalUnit unitOfTime;
	private final Period cycleLength;
	
	/**
	 * Construct a new TimeSeries object with the given parameters.
	 * @param unitOfTime the unit of time in which cycle lengths are measured in.
	 *     Defaults to {@link Years}
	 * @param period the length of the time cycle with respect to the given time unit.
	 * For example, there are 12 months in a year, so if the time unit is Years, then 
	 * the period, in months, is 12, since the length of a yearly cycle measured by months is 12.
	 * @param startTime
	 * @param series
	 */
	public TimeSeries(final TemporalUnit unitOfTime, final Period period,
			final Instant startTime, final double... series) {
		super(series);
		this.series = series;
		this.n = series.length;
		this.mean = super.mean();
		super.setName(this.name);
		this.timeIndices = new double[series.length];
		for (int i = 0; i < timeIndices.length; i++) {
			timeIndices[i] =  i;
		}
		this.unitOfTime = unitOfTime;
		this.cycleLength = period;
	}
	
	/**
	 * Construct a new TimeSeries from the underlying series data with the given start time.
	 * @param startTime the time of the first observation.
	 * @param series the observations.
	 */
	public TimeSeries(final Instant startTime, final double... series) {
		this(ChronoUnit.YEARS, Period.ofMonths(12), startTime, series);
	}
	
	/**
	 * Construct a new TimeSeries from the underlying series data with a default start at the epoch.
	 * @param series the sequence of observations.
	 */
	public TimeSeries(final double... series) {
		this(ZonedDateTime.of(1, 1, 1, 1, 1, 1, 1, ZoneId.of("UTC-05:00")).toInstant(), series);
	}
	
	/**
	 * The time series of observations.
	 * @return the time series of observations.
	 */
	public final double[] series() {
		return this.series;
	}
	
	/**
	 * The indices corresponding to the observations.
	 * @return the indices corresponding to the observations.
	 */
	public final double[] timeIndices() {
		return this.timeIndices;
	}
	
	/**
	 * The covariance of this series with itself at lag k.
	 * @param k the lag to compute the autocovariance at.
	 * @return the covariance of this series with itself at lag k.
	 */
	public final double autoCovarianceAtLag(final int k) {
		double sumOfProductOfDeviations = 0.0;
		for (int t = 0; t < n - k; t++) {
			sumOfProductOfDeviations +=  (series[t] - mean) * (series[t + k] - mean);
		}
		return sumOfProductOfDeviations/n;
	}
	
	/**
	 * The correlation of this series with itself at lag k.
	 * @param k the lag to compute the autocorrelation at.
	 * @return the correlation of this series with itself at lag k.
	 */
	public final double autoCorrelationAtLag(final int k) {
		final double variance = autoCovarianceAtLag(0);
		return autoCovarianceAtLag(k)/variance;
	}
	
	/**
	 * Every covariance measure of this series with itself up to the given lag.
	 * @param k the maximum lag to compute the autocovariance at.
	 * @return every covariance measure of this series with itself up to the given lag.
	 */
	public final double[] autoCovarianceUpToLag(final int k) {
		double[] acv = new double[Math.min(k + 1, n)];
		for (int i = 0; i < Math.min(k + 1, n); i++) {
			acv[i] = autoCovarianceAtLag(i);
		}
		return acv;
	}
	
	/**
	 * Every correlation coefficient of this series with itself up to the given lag.
	 * @param k the maximum lag to compute the autocorrelation at.
	 * @return every correlation coefficient of this series with itself up to the given lag.
	 */
	public final double[] autoCorrelationUpToLag(final int k) {
		double[] autoCorrelation = new double[Math.min(k + 1, n)];
		for (int i = 0; i < Math.min(k + 1, n); i++) {
			autoCorrelation[i] = autoCorrelationAtLag(i);
		}
		return autoCorrelation;
	}
	
	public final TimeSeries timeSlice(final int from, final int to) {
		double[] sliced = new double[to - from + 1];
		System.arraycopy(series, from - 1, sliced, 0, to - from + 1);
		return new TimeSeries(sliced);
	}
	
	/**
	 * Produce a simple line plot connecting the time indices to the observations.
	 */
	@Override
	public final void plot() {
		Plot2DPanel plot = new Plot2DPanel();
		JFrame frame = new JFrame("Time Series Plot");
		plot.addLinePlot("Series", timeIndices, series);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(600, 400);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}
	
	@Override
	public void setName(final String newName) {
		this.name = newName;
		super.setName(newName);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Time Series: ").append(name).
		append("\nObservations: ").append(Arrays.toString(series)).
		append("\nSize: ").append(n).
		append("\nMean: ").append(super.mean()).
		append("\nStandard deviation: ").append(super.stdDeviation());
		return builder.toString();
	}

}
