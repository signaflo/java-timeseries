package data;

import math.Operators;
import statistics.Statistics;

public class DataSet {
	
	private final double[] data;
	
	public DataSet(final double[] data) {
		if (data == null) {
			throw new IllegalArgumentException("Null array passed to constructor.");
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

	public final DataSet times(DataSet otherData) {
		return new DataSet(Operators.productOf(this.data, otherData.data));
	}

	public final DataSet plus(DataSet otherData) {
		return new DataSet(Operators.sumOf(this.data, otherData.data));
	}
	
	public final double variance() {
		return Statistics.varianceOf(this.data);
	}
	
	public final double stdDeviation() {
		return Statistics.stdDeviationOf(this.data);
	}
	
	public final double covariance(DataSet otherData) {
		return Statistics.covarianceOf(this.data, otherData.data);
	}
	
	public final double correlation(DataSet otherData) {
		return Statistics.covarianceOf(this.data, otherData.data);
	}
	
	public final double[] data() {
		return this.data;
	}

}
