package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.junit.Test;

public class VectorSpec {

	@Test
	public void whenTwoVectorsAddedThenResultCorrect() {
		DerivativeStructure x = new DerivativeStructure(1, 1, 0, 7);
		DerivativeStructure y = new DerivativeStructure(1, 1, 0, 8);
		DerivativeStructure z = new DerivativeStructure(1, 1, 0, 3);
		DerivativeStructure w = new DerivativeStructure(1, 1, 0, 5);
		Vector v = new Vector(new DerivativeStructure[] {x, y});
		Vector u = new Vector(new DerivativeStructure[] {z, w});
		Vector vu = v.plus(u);
		DerivativeStructure[] result = vu.points();
		for (DerivativeStructure ds : result) {
			double[] derivs = ds.getAllDerivatives();
			for (double d : derivs) {
				System.out.println(d);
			}
		}
	}
	@Test
	public void whenTwoVectorsDotProductThenResultCorrect() {
		DerivativeStructure[] x = new DerivativeStructure[2];
		DerivativeStructure[] y = new DerivativeStructure[2];
		int nParams = 4;
		int order = 1;
	    x[0] = new DerivativeStructure(nParams, order, 0, 7);
	}
}
