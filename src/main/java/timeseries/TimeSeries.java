package timeseries;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.plots.LinePlot;

import data.DataSet;
import data.Doubles;

/**
 * A sequence of observations taken at regular time intervals.
 * @author Jacob Rachiele
 *
 */
public final class TimeSeries extends DataSet {
	
	private final TemporalUnit timeScale;
	private final int n;
	private final double mean;
	private final double[] series;
	private final double[] timeIndices;
	private String name = "Time Series";
	private final List<OffsetDateTime> observationTimes;
	private final long periodLength;
	
	
	/**
	 * Construct a new TimeSeries object with the given parameters.
	 * @param timeScale The scale of time at which observations are made (or aggregated). Time series observations
	 *   are commonly made (or aggregated) on a yearly, monthly, weekly, daily, hourly, etc... basis.
	 * @param startTime The time at which the first observation was made. Usually a rough approximation.
	 * @param periodLength The length of time between observations measured in the units given by the
	 *   <code>timeScale</code> argument. For example, quarterly data could be provided with a 
	 *   timeScale of {@link ChronoUnit#MONTHS} and a periodLength of 3.
	 * @param series The data constituting this TimeSeries.
	 */
	public TimeSeries(final TemporalUnit timeScale, final OffsetDateTime startTime,
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
		this.timeScale = timeScale;
		this.periodLength = periodLength;
		this.observationTimes = new ArrayList<>(series.length);
		observationTimes.add(startTime);
		for (int i = 1; i < series.length; i++) {
			observationTimes.add(observationTimes.get(i - 1).plus(periodLength, timeScale));
		}
	}
	
	private TimeSeries(final TemporalUnit timeScale, final List<OffsetDateTime> observationTimes,
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
		this.timeScale = timeScale;
		this.periodLength = periodLength;
		this.observationTimes = observationTimes;
	}
	
	/**
	 * Construct a new TimeSeries from the given data with the supplied start time.
	 * @param startTime the time of the first observation.
	 * @param series the observations.
	 */
	TimeSeries(final OffsetDateTime startTime, final double... series) {
		this(ChronoUnit.MONTHS, startTime, 1L, series);
	}
	
	/**
	 * Construct a new TimeSeries from the given data counting from year 1. Use
	 * this constructor if the dates and/or times associated with the observations do not matter.
	 * @param series the sequence of observations.
	 */
	public TimeSeries(final double... series) {
		this(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0)), series);
	}
	
	/**
	 * The time series of observations.
	 * @return the time series of observations.
	 */
	public final double[] series() {
		return this.series;
	}
	
	public final double[] timeIndices() {
		return this.timeIndices;
	}
	
	public final double at(final int index) {
		return this.series[index];
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
		final double[] acv = new double[Math.min(k + 1, n)];
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
		final double[] autoCorrelation = new double[Math.min(k + 1, n)];
		for (int i = 0; i < Math.min(k + 1, n); i++) {
			autoCorrelation[i] = autoCorrelationAtLag(i);
		}
		return autoCorrelation;
	}
	
	public final TimeSeries slice(final int from, final int to) {
		final double[] sliced = new double[to - from + 1];
		System.arraycopy(series, from, sliced, 0, to - from + 1);
		final List<OffsetDateTime> obsTimes = new ArrayList<>(this.observationTimes.subList(from, to + 1));
		return new TimeSeries(this.timeScale, obsTimes, this.periodLength, sliced);
	}
	
	// ********** Plots ********** //
	
	/**
	 * Produce a simple line plot connecting the time indices to the observations.
	 */
	@Override
	public final void plot() {
		final Plot2DPanel plot = new Plot2DPanel();
		final JFrame frame = new JFrame("Time Series Plot");
		plot.addLinePlot("Series", series);
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
		
//		CategoryChart chart = new CategoryChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800).
//				title("Autocorrelations By Lag").build();

		
//		XYSeries series = chart.addSeries("Acf", lags, acf);
//		XYSeries series2 = chart.addSeries("Upper Bound", lags, upperLine);
//		XYSeries series3 = chart.addSeries("Lower Bound", lags, lowerLine);
//		chart.getStyler().setChartFontColor(Color.BLACK).setSeriesColors(new Color[] {Color.BLUE,
//				Color.RED, Color.RED});
//		series.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
//		
//		series2.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
//		series2.setMarker(SeriesMarkers.NONE);
//		series3.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
//		series3.setMarker(SeriesMarkers.NONE);
//		new SwingWrapper<>(chart).displayChart();
		
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
	public final void setName(final String newName) {
		this.name = newName;
		super.setName(newName);
	}
	
	@Override
	public final String getName() {
		return this.name;
	}
	
	public final TimeSeries copy() {
		return new TimeSeries(this);
	}
	
	private TimeSeries(final TimeSeries original) {
		super(original);
		this.mean = original.mean;
		this.n = original.n;
		this.name = original.name;
		// Note OffsetDateTime is immutable.
		this.observationTimes = new ArrayList<>(original.observationTimes);
		this.periodLength = original.periodLength;
		this.series = original.series.clone();
		this.timeIndices = original.timeIndices.clone();
		this.timeScale = original.timeScale;
	}
	
	public final TemporalUnit timeScale() {
		return this.timeScale;
	}
	
	public final List<OffsetDateTime> observationTimes() {
		return this.observationTimes;
	}
	
	public final long periodLength() {
		return this.periodLength;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("n: ").append(n).append("\nmean: ").append(mean).append("\nseries: ")
				.append(Arrays.toString(Doubles.slice(series, 0, 3))).append(" ... ")
				.append(Arrays.toString(Doubles.slice(series, n - 3, n))).append("\nname: ").append(name)
				.append("\nobservationTimes: ").append(observationTimes.subList(0, 3)).append(" ... ")
				.append(observationTimes.subList(n - 3, n)).append("\nperiodLength: ")
				.append(periodLength).append(" " + timeScale).append("\ntimeScale: ").append(timeScale);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(mean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + n;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((observationTimes == null) ? 0 : observationTimes.hashCode());
		result = prime * result + (int) (periodLength ^ (periodLength >>> 32));
		result = prime * result + Arrays.hashCode(series);
		result = prime * result + Arrays.hashCode(timeIndices);
		result = prime * result + ((timeScale == null) ? 0 : timeScale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TimeSeries other = (TimeSeries) obj;
		if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) {
			return false;
		}
		if (n != other.n) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (observationTimes == null) {
			if (other.observationTimes != null) {
				return false;
			}
		} else if (!observationTimes.equals(other.observationTimes)) {
			return false;
		}
		if (periodLength != other.periodLength) {
			return false;
		}
		if (!Arrays.equals(series, other.series)) {
			return false;
		}
		if (!Arrays.equals(timeIndices, other.timeIndices)) {
			return false;
		}
		if (timeScale == null) {
			if (other.timeScale != null) {
				return false;
			}
		} else if (!timeScale.equals(other.timeScale)) {
			return false;
		}
		return true;
	}

}
