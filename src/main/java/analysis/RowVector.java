package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public final class RowVector implements Vector {
	
	private final DerivativeStructure[] points;
	private final int nRows = 1;
	private final int nCols;
	
	RowVector(final DerivativeStructure[] points) {
		this.points = points;
		this.nCols = points.length;
	}
	
	final RowVector plus(final RowVector other) {
		if (this.points.length != other.points.length) {
			throw new IllegalArgumentException("The vectors must have the same length");
		}
		final DerivativeStructure[] result = new DerivativeStructure[this.points.length];
		for (int i = 0; i < points.length; i++) {
			result[i] = this.points[i].add(other.points[i]);
		}
		return new RowVector(result);
	}
	
	public final DerivativeStructure[] points() {
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
		return new ColumnVector(this.points);
	}


}
