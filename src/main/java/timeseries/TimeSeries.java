package timeseries;

import java.awt.Color;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plots.LinePlot;

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
	private final List<ZonedDateTime> observationTimes;
	
	/**
	 * Construct a new TimeSeries object with the given parameters.
	 * @param timeScale The scale of time at which observations are made (or aggregated). Time series observations
	 *   are commonly made (or aggregated) on a yearly, monthly, weekly, daily, hourly, etc... basis.
	 * @param startTime The time at which the first observation was made. Usually a rough approximation.
	 * @param periodLength The length of time between observations measured in the units given by the
	 *   <code>timeScale</code> argument. For example, quarterly data, though not required, should likely be 
	 *   provided with a timeScale of {@link ChronoUnit#MONTHS} and a periodLength of 3.
	 * @param series The data constituting this TimeSeries.
	 */
	public TimeSeries(final TemporalUnit timeScale, final ZonedDateTime startTime,
			final long periodLength, final double... series) {
		super(series);
		this.series = series;
		this.n = series.length;
		this.mean = super.mean();
		super.setName(this.name);
		this.timeIndices = new double[series.length];
		for (int i = 0; i < timeIndices.length; i++) {
			timeIndices[i] =  i;
		}
		this.observationTimes = new ArrayList<>(series.length);
		observationTimes.add(startTime);
		for (int i = 1; i < series.length; i++) {
			observationTimes.add(observationTimes.get(i - 1).plus(periodLength, timeScale));
		}
	}
	
	/**
	 * Construct a new TimeSeries from the given data with the supplied start time.
	 * @param startTime the time of the first observation.
	 * @param series the observations.
	 */
	TimeSeries(final ZonedDateTime startTime, final double... series) {
		this(ChronoUnit.MONTHS, startTime, 1L, series);
	}
	
	/**
	 * Construct a new TimeSeries from the given data counting from year 1. Use
	 * this constructor if the dates and/or times associated with the observations do not matter.
	 * @param series the sequence of observations.
	 */
	public TimeSeries(final double... series) {
		this(ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("America/Chicago")), series);
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
	
	// ********** Plots ********** //
	
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
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}
	
	public final void plotAcf() {
		final int k = 20;
		final double[] acf = autoCorrelationUpToLag(k);
		final double[] lags = new double[k + 1];
		for (int i = 0; i < lags.length; i++) {
			lags[i] = i;
		}
		final double upper = (-1/series.length) + (2/Math.sqrt(series.length));
		final double lower = (-1/series.length) - (2/Math.sqrt(series.length));
		final double[][] upperLine = new double[lags.length][lags.length];
		final double[][] lowerLine = new double[lags.length][lags.length];
		for (int i = 0; i < lags.length; i++) {
			upperLine[i] = new double[] {i, upper};
		}
		for (int i = 0; i < lags.length; i++) {
			lowerLine[i] = new double[] {i, lower};
		}
		
		final LinePlot lineUp = new LinePlot("Upper Bound", Color.RED, upperLine);
		final LinePlot lineDown = new LinePlot("Lower Bound", Color.RED, lowerLine);
		final Plot2DPanel plot = new Plot2DPanel();
		final JFrame frame = new JFrame("Acf Plot");
		plot.addBarPlot("Autocorrelations to Lag 20", lags, acf);
		plot.addPlot(lineUp);
		plot.addPlot(lineDown);
		plot.setFixedBounds(1, Math.floor(lower*10)/10, 1.0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}
	
	// ********** Plots ********** //
	
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
