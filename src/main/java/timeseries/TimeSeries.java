package timeseries;

import java.util.Arrays;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import data.DataSet;

/**
 * A sequence of observations taken at regular time intervals.
 * @author jacob
 *
 */
public final class TimeSeries extends DataSet {
	
	private final int n;
	private final double mean;
	private final double[] series;
	private final double[] timeIndices;
	
	/**
	 * Construct a new TimeSeries from the underlying series data with the given start time.
	 * @param startTime the time of the first observation.
	 * @param series the observations.
	 */
	public TimeSeries(int startTime, final double... series) {
		super(series);
		this.series = series;
		this.n = series.length;
		this.mean = super.mean();
		this.timeIndices = new double[series.length];
		for (int i = 0; i < timeIndices.length; i++) {
			timeIndices[i] = startTime + i;
		}
	}
	
	/**
	 * Construct a new TimeSeries from the underlying series data with a default start time of 1.
	 * @param series the sequence of observations.
	 */
	public TimeSeries(final double... series) {
		this(1, series);
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("series: ").append(Arrays.toString(series)).
		append("\nsize: ").append(n).
		append("\nmean: ").append(super.mean()).
		append("\nstandard deviation: ").append(super.stdDeviation());
		return builder.toString();
	}

}
