package data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

public class OperatorsSpec {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private double[] data1;
	
	@Before
	public void beforeMethod() {
	  data1 = new double[] {3.0, 5.0};
	}
	
	@Test
	public void whenProductOfCalledWithDiffereningLengthExceptionThrown() {
		double[] data2 = new double[] {4.0, 5.0, 6.0};
		exception.expect(IllegalArgumentException.class);
		Operators.productOf(data1, data2);
	}
	
	@Test
	public void whenProductOfCalledThenVectorizedProductReturned() {
		double[] data2 = new double[] {4.0, 5.0};
		double[] expected = new double[] {12.0, 25.0};
		assertThat(Operators.productOf(data1, data2), is(equalTo(expected)));
	}
	
	@Test
	public void whenSumOfCalledWithDiffereningLengthExceptionThrown() {
		double[] data2 = new double[] {4.0, 5.0, 6.0};
		exception.expect(IllegalArgumentException.class);
		Operators.sumOf(data1, data2);
	}
	
	 @Test
	  public void whenDifferenceOfCalledWithDiffereningLengthExceptionThrown() {
	    double[] data2 = new double[] {4.0, 5.0, 6.0};
	    exception.expect(IllegalArgumentException.class);
	    Operators.differenceOf(data1, data2);
	  }
	 
	  @Test
	  public void whenQuotientOfCalledWithDiffereningLengthExceptionThrown() {
	    double[] data2 = new double[] {4.0, 5.0, 6.0};
	    exception.expect(IllegalArgumentException.class);
	    Operators.quotientOf(data1, data2);
	  }
	
	@Test
	public void whenSumOfMethodThenVectorizedSumReturned() {
		double[] data2 = new double[] {4.0, 5.0};
		double[] expected = new double[] {7.0, 10.0};
		assertThat(Operators.sumOf(data1, data2), is(equalTo(expected)));
	}
	
	@Test
	public void whenScaledMethodThenDataScaled() {
	  double[] expected = new double[] {1.5, 2.5};
	  assertThat(Operators.scale(data1, 0.5), is(equalTo(expected)));
	}
	
	@Test
	public void whenQuotientOfMethodThenDataDivided() {
	  double[] expected = new double[] {1.5, 1.25};
	  assertThat(Operators.quotientOf(data1, new double[] {2.0, 4.0}), is(equalTo(expected)));
	}

}
