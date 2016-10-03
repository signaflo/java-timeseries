package operators;

import org.junit.Test;

import data.TestData;
import timeseries.TimeSeries;
import timeseries.operators.LagPolynomial;

public final class LagPolynomialSpec {
  
  @Test
  public void testLagPoly() {
    TimeSeries series = TestData.ausbeerSeries();
    LagPolynomial poly = new LagPolynomial(-2, 1);
    System.out.println(poly);
    LagPolynomial poly2 = new LagPolynomial(0.65);
    System.out.println(poly2);
    LagPolynomial product = poly.multiply(poly2);
    System.out.println(product);
    System.out.println(poly.applyInverse(series, 3) + series.difference(1, 2).at(1));
  }

}
