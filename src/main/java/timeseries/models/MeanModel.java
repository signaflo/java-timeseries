package timeseries.models;

import java.time.OffsetDateTime;

import data.Doubles;
import timeseries.TimeSeries;

/**
 * This time series model assumes that the series contains no underlying trend or seasonality, 
 * and that early values of the series provide as much information as recent values.
 * @author jacob
 *
 */
public final class MeanModel {
	
	private final TimeSeries timeSeries;
	private final TimeSeries fittedSeries;
	private final double mean;
	
	public MeanModel(final TimeSeries observed) {
		this.timeSeries = observed.copy();
		this.mean = observed.mean();
		this.fittedSeries = new TimeSeries(observed.timeScale(), observed.observationTimes().get(0),
				observed.periodLength(), Doubles.fill(observed.n(), this.mean));
	}
	
	public final TimeSeries forecast(final int steps) {
		final double[] forecasted = Doubles.fill(steps, this.mean);
		final OffsetDateTime startTime = this.timeSeries.observationTimes().get(this.timeSeries.n() - 1)
				.plus(this.timeSeries.periodLength(), this.timeSeries.timeScale());
		final TimeSeries forecastSeries = new TimeSeries(this.timeSeries.timeScale(), startTime,
				this.timeSeries.periodLength(), forecasted);
		forecastSeries.setName(this.timeSeries.getName() + " " + steps + " step ahead forecast");
		return forecastSeries;
	}

}
