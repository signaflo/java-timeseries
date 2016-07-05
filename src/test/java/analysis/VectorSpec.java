package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.junit.Test;

public class VectorSpec {

	@Test
	public void testVector() {
		DerivativeStructure x = new DerivativeStructure(2, 1, 0, 7);
		DerivativeStructure y = new DerivativeStructure(2, 1, 1, 8);
		DerivativeStructure z = new DerivativeStructure(2, 1, 3);
		DerivativeStructure w = new DerivativeStructure(2, 1, 5);
		Vector v = new Vector(new DerivativeStructure[] {x, y});
		Vector u = new Vector(new DerivativeStructure[] {z, w});
		Vector vu = v.add(u);
		DerivativeStructure[] result = vu.points();
		for (DerivativeStructure ds : result) {
			double[] derivs = ds.getAllDerivatives();
			for (double d : derivs) {
				System.out.println(d);
			}
		}
	}
}
