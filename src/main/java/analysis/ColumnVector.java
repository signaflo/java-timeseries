package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class ColumnVector implements Vector {
	
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
	
	@Override
	public final int nRows() {
		return this.nRows;
	}
	
	@Override
	public final int nCols() {
		return this.nCols;
	}

	@Override
	public final Vector transpose() {
		return new RowVector(this.points);
	}
}
