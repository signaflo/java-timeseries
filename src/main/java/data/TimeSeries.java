package data;

public final class TimeSeries extends DataSet {
	
	private final double[] series;
	
	public TimeSeries(final double[] series) {
		super(series);
		this.series = series;
	}

}
