package math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OperatorsSpec {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void whenProductOfCalledWithDiffereningLengthExceptionThrown() {
		double[] data1 = new double[] {3.0, 5.0};
		double[] data2 = new double[] {4.0, 5.0, 6.0};
		exception.expect(IllegalArgumentException.class);
		Operators.productOf(data1, data2);
	}

}
