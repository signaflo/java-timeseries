package timeseries.models;

import java.time.OffsetDateTime;

import data.DoubleFunctions;
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
		this.mean = this.timeSeries.mean();
		this.fittedSeries = new TimeSeries(observed.timeScale(), observed.observationTimes().get(0),
				observed.periodLength(), DoubleFunctions.fill(observed.n(), this.mean));
	}
	
	public final TimeSeries forecast(final int steps) {
		final double[] forecasted = DoubleFunctions.fill(steps, this.mean);
		final OffsetDateTime startTime = this.timeSeries.observationTimes().get(this.timeSeries.n() - 1)
				.plus(this.timeSeries.periodLength(), this.timeSeries.timeScale());
		final TimeSeries forecastSeries = new TimeSeries(this.timeSeries.timeScale(), startTime,
				this.timeSeries.periodLength(), forecasted);
		forecastSeries.setName(this.timeSeries.getName() + " " + steps + " step ahead forecast");
		return forecastSeries;
	}
	
	public final TimeSeries fittedSeries() {
		return this.fittedSeries;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("timeSeries: ").append(timeSeries).append("\nfittedSeries: ").append(fittedSeries)
				.append("\nmean: ").append(mean);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fittedSeries == null) ? 0 : fittedSeries.hashCode());
		long temp;
		temp = Double.doubleToLongBits(mean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		MeanModel other = (MeanModel) obj;
		if (fittedSeries == null) {
			if (other.fittedSeries != null) {
				return false;
			}
		} else if (!fittedSeries.equals(other.fittedSeries)) {
			return false;
		}
		if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) {
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
