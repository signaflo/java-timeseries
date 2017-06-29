/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package math.operations;

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
