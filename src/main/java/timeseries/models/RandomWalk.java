package timeseries.models;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.None;

import stats.distributions.Distribution;
import stats.distributions.NormalDistribution;

import timeseries.TimeSeries;

public final class RandomWalk {
	
	private final TimeSeries timeSeries;
	private final TimeSeries fittedSeries;
	private final TimeSeries residuals;
	
	public RandomWalk(final TimeSeries observed) {
		this.timeSeries = observed.copy();
		this.fittedSeries = fitSeries();
		this.residuals = calculateResiduals();
		
		this.fittedSeries.setName("Fitted Series");
		this.residuals.setName("Residuals");
	}
	
	/**
	 * Simulate a random walk assuming that the errors follow the given Distribution.
	 * @param dist The probability distribution that observations are drawn from.
	 * @param n The number of observations to simulate.
	 * @return A simulated RandomWalk model.
	 */
	public static final RandomWalk simulate(final Distribution dist, final int n) {
		final double[] series = new double[n];
		for (int t = 0; t < n; t++) {
			series[t] = dist.rand();
		}
		return new RandomWalk(new TimeSeries(series));
	}
	
	/**
	 * Simulate a random walk assuming errors follow a Normal (Gaussian) Distribution with the given mean and 
	 * standard deviation.
	 * @param mean The mean of the Normal distribution the observations are drawn from.
	 * @param sigma The standard deviation of the Normal distribution the observations are drawn from.
	 * @param n The number of observations to simulate.
	 * @return
	 */
	public static final RandomWalk simulate(final double mean, final double sigma, final int n) {
		final Distribution dist = new NormalDistribution(mean, sigma);
		return simulate(dist, n);
	}
	
	public static final RandomWalk simulate(final double sigma, final int n) {
		final Distribution dist = new NormalDistribution(0, sigma);
		return simulate(dist, n);
	}
	
	public static final RandomWalk simulate(final int n) {
		final Distribution dist = new NormalDistribution(0, 1);
		return simulate(dist, n);
	}
	
	public final TimeSeries forecast(final int steps) {
		final int n = timeSeries.n();
		final double[] forecast = new double[steps];
		for (int t = 0; t < steps; t++) {
			forecast[t] = timeSeries.at(n - 1);
		}
		final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1).plus(timeSeries.periodLength(),
				timeSeries.timeScale());
		return new TimeSeries(timeSeries.timeScale(), startTime, timeSeries.periodLength(), forecast);
	}
	
	public final TimeSeries timeSeries() {
		return this.timeSeries.copy();
	}
	
	public final TimeSeries fittedSeries() {
		return this.fittedSeries.copy();
	}

	public final TimeSeries residuals() {
	    return this.residuals.copy();
	}
	
	public final void plotFit() {
		final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
		for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
			xAxis.add(Date.from(dateTime.toInstant()));
		}
		List<Double> seriesList = com.google.common.primitives.Doubles.asList(timeSeries.series());
		List<Double> fittedList = com.google.common.primitives.Doubles.asList(fittedSeries.series());
		final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
				.title("Random Walk Fitted and Actual").build();
		XYSeries fitSeries = chart.addSeries("Fitted Values", xAxis, fittedList);
		XYSeries observedSeries = chart.addSeries("Observed Values", xAxis, seriesList);
		XYStyler styler = chart.getStyler();
		styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		observedSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
		observedSeries.setMarker(new None()).setLineColor(Color.RED);
		fitSeries.setMarker(new None()).setLineColor(Color.BLUE);
		
		new SwingWrapper<>(chart).displayChart();
	}
	
	public final void plotResiduals() {
		final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
		for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
			xAxis.add(Date.from(dateTime.toInstant()));
		}
		List<Double> seriesList = com.google.common.primitives.Doubles.asList(residuals.series());
		final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
				.title("Random Walk Fitted and Actual").build();
		XYSeries residualSeries = chart.addSeries("Model Residuals", xAxis, seriesList);
		residualSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
		residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);	
		
		new SwingWrapper<>(chart).displayChart();
	}
	private final TimeSeries fitSeries() {
		final double[] fitted = new double[timeSeries.n()];
		fitted[0] = timeSeries.at(0);
		for (int t = 1; t < timeSeries.n(); t++) {
			fitted[t] = timeSeries.at(t - 1);
		}
		return new TimeSeries(timeSeries.timeScale(), timeSeries.observationTimes().get(0), 
				timeSeries.periodLength(), fitted);
	}
	
	private final TimeSeries calculateResiduals() {
		final double[] residuals = new double[timeSeries.n()];
		for (int t = 1; t < timeSeries.n(); t++) {
			residuals[t] = timeSeries.at(t) - fittedSeries.at(t);
		}
		return new TimeSeries(timeSeries.timeScale(), timeSeries.observationTimes().get(0), 
				timeSeries.periodLength(), residuals);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("timeSeries: ").append(timeSeries).append("\nfittedSeries: ").append(fittedSeries)
				.append("\nresiduals: ").append(residuals);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fittedSeries == null) ? 0 : fittedSeries.hashCode());
		result = prime * result + ((residuals == null) ? 0 : residuals.hashCode());
		result = prime * result + ((timeSeries == null) ? 0 : timeSeries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RandomWalk other = (RandomWalk) obj;
		if (fittedSeries == null) {
			if (other.fittedSeries != null) {
				return false;
			}
		} else if (!fittedSeries.equals(other.fittedSeries)) {
			return false;
		}
		if (residuals == null) {
			if (other.residuals != null) {
				return false;
			}
		} else if (!residuals.equals(other.residuals)) {
			return false;
		}
		if (timeSeries == null) {
			if (other.timeSeries != null) {
				return false;
			}
		} else if (!timeSeries.equals(other.timeSeries)) {
			return false;
		}
		return true;
	}
}
