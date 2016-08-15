package timeseries.models;

import java.time.OffsetDateTime;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import smile.stat.distribution.Distribution;
import smile.stat.distribution.GaussianDistribution;
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
	 * @param dist
	 * @param n
	 * @return
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
	 * @param mean
	 * @param sigma
	 * @param n
	 * @return
	 */
	public static final RandomWalk simulate(final double mean, final double sigma, final int n) {
		final Distribution dist = new GaussianDistribution(mean, sigma);
		return simulate(dist, n);
	}
	
	public static final RandomWalk simulate(final double sigma, final int n) {
		final Distribution dist = new GaussianDistribution(0, sigma);
		return simulate(dist, n);
	}
	
	public static final RandomWalk simulate(final int n) {
		final Distribution dist = new GaussianDistribution(0, 1);
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
	
	public final TimeSeries fittedSeries() {
		return this.fittedSeries;
	}

	public final TimeSeries residuals() {
	    return this.residuals;
	}
	
	public final void plot() {
		final Plot2DPanel plot = new Plot2DPanel();
		final JFrame frame = new JFrame("Time Series Plot");
		plot.addLinePlot("Scatter", fittedSeries.series());
		plot.addScatterPlot("Series", timeSeries.series());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}
	
	public final void residualPlot() {
		final Plot2DPanel plot = new Plot2DPanel();
		final JFrame frame = new JFrame("Time Series Plot");
		plot.addScatterPlot("Series", residuals.series());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
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
