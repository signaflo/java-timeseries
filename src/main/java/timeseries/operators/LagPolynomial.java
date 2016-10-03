package timeseries.operators;

import data.DoubleFunctions;
import timeseries.TimeSeries;

public class LagPolynomial {
  
  final double[] parameters;
  final double[] coefficients;
  final int degree;
  
  public LagPolynomial(final double... parameters) {
    this.parameters = parameters.clone();
    this.coefficients = new double[parameters.length + 1];
    this.coefficients[0] = 1.0;
    for (int i = 0; i < parameters.length; i++) {
      this.coefficients[i + 1] = parameters[i];
    }
    this.degree = parameters.length;
  }
  
  public final double[] parameters() {
    return this.parameters.clone();
  }
  
  public final LagPolynomial multiply(final LagPolynomial other) {
    final double[] newParams = new double[this.degree + other.degree + 1];
    for (int i = 0; i < coefficients.length; i++) {
      for (int j = 0; j < other.coefficients.length; j++) {
        newParams[i + j] += coefficients[i] * other.coefficients[j];
      }
    }
    return new LagPolynomial(DoubleFunctions.slice(newParams, 1, newParams.length));
  }
  
  public final double apply(final TimeSeries timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < coefficients.length; i++) {
      value += this.coefficients[i] * LagOperator.apply(timeSeries, index, i);
    }
    return value;
  }
  
  public double applyInverse(final TimeSeries timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value -= parameters[i] * LagOperator.apply(timeSeries, index, i + 1);
    }
    return value;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("1");
    for (int i = 1; i < coefficients.length - 1; i++) {
      if (coefficients[i] < 0) {
        builder.append(" - ");
      } else {
        builder.append(" + ");
      }
      if (coefficients[i] != 1.0) {
        builder.append(Math.abs(coefficients[i]));
      }
      builder.append("L");
      if (i > 1) {
        builder.append("^").append(i);
      }
    }
    final int lastIndex = coefficients.length - 1;
    if (coefficients[lastIndex] < 0) {
      builder.append(" - ");
    } else {
      builder.append(" + ");
    }
    if (coefficients.length  > 1) {
      if (coefficients[lastIndex] != 1.0) {
      builder.append(Math.abs(coefficients[lastIndex]));
      }
      builder.append("L");
    }
    if (coefficients.length > 2) {
      builder.append("^").append(lastIndex);
    }
    return builder.toString();
  }
}
