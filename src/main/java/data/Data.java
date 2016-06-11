package data;

import statistics.Statistics;

public final class Data {
	
	private final double[] data;
	
	public Data(final double[] data) {
		if (data == null) {
			throw new IllegalArgumentException("The data cannot be null");
		}
		this.data = data;
	}
	
	public final double sum() {
		return Statistics.sumOf(this.data);
	}

	public final double mean() {
		return Statistics.meanOf(this.data);
	}

	public final int n() {
		return this.data.length;
	}

}
