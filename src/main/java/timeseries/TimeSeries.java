package timeseries;

import data.DataSet;

public final class TimeSeries extends DataSet {
	
	private final double[] series;
	
	public TimeSeries(final double[] series) {
		super(series);
		this.series = series;
	}
	
	public final double[] series() {
		return this.series;
	}

}
