package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class ColumnVector {
	
	private final DerivativeStructure[] points;
	private final int nCols = 1;
	private final int nRows;
	
	ColumnVector(DerivativeStructure[] points) {
		this.points = points;
		this.nRows = points.length;
	}
	
	ColumnVector plus(ColumnVector other) {
		if (this.points.length != other.points.length) {
			throw new IllegalArgumentException("The vectors must have the same length");
		}
		final DerivativeStructure[] result = new DerivativeStructure[this.points.length];
		for (int i = 0; i < points.length; i++) {
			result[i] = this.points[i].add(other.points[i]);
		}
		return new ColumnVector(result);
	}
	
	public DerivativeStructure[] points() {
		return this.points;
	}
	
	public final int nRows() {
		return this.nRows;
	}
	
	public final int nCols() {
		return this.nCols;
	}

}
