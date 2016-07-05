package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.junit.Test;

public class SumOfSquaresSpec {
	
	@Test
	public void testSSQ() {
		int nParams = 2;
		int order = 1;
		DerivativeStructure x = new DerivativeStructure(nParams, order, 0, 3.0);
		DerivativeStructure y = new DerivativeStructure(nParams, order, 1, 4.0);
		SumOfSquares ssq = new SumOfSquares();
		DerivativeStructure result = ssq.value(new DerivativeStructure[] {x, y});
		double[] derivs = result.getAllDerivatives();
		for (double d : derivs) {
			System.out.println(d);
		}
	}

}
