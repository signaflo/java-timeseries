package data.generic;

import math.Complex;

import math.Rational;
import math.Real;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NumericalDataSetSpec {

    private DataSet<Complex> dataSet = getDataSetOne();

    @Test
    public void whenSumThenComputedCorrectly() {
        Complex expectedSum = new Complex(3.7, 11.2);
        assertThat(dataSet.sum(), is(expectedSum));
    }

    @Test
    public void whenSumOfSquaresThenComputedCorrectly() {
        Complex expectedSumOfSquares = new Complex(-27.29, 39.260000000000005);
        assertThat(dataSet.sumOfSquares(), is(expectedSumOfSquares));
    }

    @Test
    public void whenMeanThenComputedCorrectly() {
        Complex expectedMean = new Complex(1.2333333333333334, 3.733333333333333);
        assertThat(dataSet.mean(), is(expectedMean));
    }

    @Test
    public void whenTimesThenComputedCorrectly() {
        List<Complex> product = Arrays.asList(
                new Complex(0.5, -27.5),
                new Complex(-11.100000000000001, 7.199999999999999),
                new Complex(-9.35, 13.75)
                                             );
        DataSet<Complex> productDataSet = new NumericalDataSet<>(product);
        assertThat(dataSet.times(getDataSetTwo()), is(productDataSet));
    }

    @Test
    public void whenVarianceThenComputedCorrectly() {
        assertThat(dataSet.variance(), is(new Complex(8.106666666666667)));
    }

    @Test
    public void whenCovarianceThenComputedCorrectly() {
        assertThat(dataSet.covariance(getDataSetTwo()),
                   is(new Complex(-13.233333333333333, 5.466666666666667)));
    }
    @Test
    public void whenCorrelationThenComputedCorrectly() {
        assertThat(dataSet.correlation(getDataSetTwo()),
                   is(new Complex(-0.8438942452156876, 0.34861122472386086)));
    }

    @Test
    public void whenMedianWithEvenSizeSetThenCorrectValue() {
        List<Complex> data = Arrays.asList(
                new Complex(5.0, 3.0),
                new Complex(10.0, 5.0),
                new Complex(2.0, 0.0),
                new Complex(-3.5, 7.5));
        DataSet<Complex> dataSet = new NumericalDataSet<>(data);
        assertThat(dataSet.median(), is(new Complex(0.75, 5.25)));
    }

    @Test
    public void whenMedianWithOddSizeSetThenCorrectValue() {
        assertThat(dataSet.median(), is(new Complex(2.4, 3.7)));
    }

    private DataSet<Complex> getDataSetOne() {
        Complex c1 = new Complex(3, 5);
        Complex c2 = new Complex(2.4, 3.7);
        Complex c3 = new Complex(-1.7, 2.5);
        List<Complex> values = Arrays.asList(c1, c2, c3);
        return new NumericalDataSet<>(values);
    }

    private DataSet<Complex> getDataSetTwo() {
        Complex c1 = new Complex(-4, -2.5);
        Complex c2 = new Complex(0.0, 3.0);
        Complex c3 = new Complex(5.5);
        List<Complex> values = Arrays.asList(c1, c2, c3);
        return new NumericalDataSet<>(values);
    }

    private DataSet<Real> getDataSetThree() {
        Real r1 = new Real(-4.0);
        Real r2 = new Real(3.0);
        Real r3 = new Real(5.5);
        List<Real> values = Arrays.asList(r1, r2, r3);
        return new NumericalDataSet<>(values);
    }

    private DataSet<Rational> getDataSetFour() {
        Rational r1 = new Rational(3, 4);
        Rational r2 = new Rational(7, 9);
        Rational r3 = new Rational(4);
        List<Rational> values = Arrays.asList(r1, r2, r3);
        return new NumericalDataSet<>(values);
    }


}
