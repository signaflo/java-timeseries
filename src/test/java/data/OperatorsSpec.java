package data;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import data.Operators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

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
	
	@Test
	public void whenProductOfCalledThenVectorizedProductReturned() {
		double[] data1 = new double[] {3.0, 5.0};
		double[] data2 = new double[] {4.0, 5.0};
		double[] expected = new double[] {12.0, 25.0};
		assertThat(Operators.productOf(data1, data2), is(equalTo(expected)));
	}
	
	@Test
	public void whenSumOfCalledWithDiffereningLengthExceptionThrown() {
		double[] data1 = new double[] {3.0, 5.0};
		double[] data2 = new double[] {4.0, 5.0, 6.0};
		exception.expect(IllegalArgumentException.class);
		Operators.sumOf(data1, data2);
	}
	
	@Test
	public void whenSumOfCalledThenVectorizedSumReturned() {
		double[] data1 = new double[] {3.0, 5.0};
		double[] data2 = new double[] {4.0, 5.0};
		double[] expected = new double[] {7.0, 10.0};
		assertThat(Operators.sumOf(data1, data2), is(equalTo(expected)));
	}

}
