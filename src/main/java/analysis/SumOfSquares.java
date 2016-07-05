package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;

final class SumOfSquares implements MultivariateDifferentiableFunction {
	
	
	@Override
	public final DerivativeStructure value(final DerivativeStructure[] point) {
		DerivativeStructure sum = point[0].multiply(point[0]);
		for (int i = 1; i < point.length; i++) {
			sum = sum.add(point[i].multiply(point[i]));
		}
		
		return sum;
	}
	
	@Override
	public final double value(final double[] point) {
		return 0.0;
	}

}
