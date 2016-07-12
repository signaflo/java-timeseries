package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class Vector {
	
	private final DerivativeStructure[] points;
	
	Vector(DerivativeStructure[] points) {
		this.points = points;
	}
	
	Vector plus(Vector other) {
		if (this.points.length != other.points.length) {
			throw new IllegalArgumentException("The vectors must have the same length");
		}
		final DerivativeStructure[] result = new DerivativeStructure[this.points.length];
		for (int i = 0; i < points.length; i++) {
			result[i] = this.points[i].add(other.points[i]);
		}
		return new Vector(result);
	}
	
	public DerivativeStructure[] points() {
		return this.points;
	}

}
