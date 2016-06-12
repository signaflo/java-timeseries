package data;

import math.Operators;
import statistics.Statistics;

public final class DataSet {
	
	private final double[] data;
	
	public DataSet(final double[] data) {
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

	public final double[] times(DataSet otherData) {
		return Operators.productOf(this.data, otherData.data);
	}

	public final double[] plus(DataSet otherData) {
		return Operators.sumOf(this.data, otherData.data);
	}

}
