/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package data;

/**
 * Static methods for performing vector operations on arrays.
 * @author Jacob Rachiele
 *
 */
public final class Operators {

	private Operators(){}
	
	public static final double[] productOf(final double[] data1, final double[] data2) {
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("The data arrays must have the same length.");
		}
		final double[] product = new double[data1.length];
		for (int i = 0; i < data1.length; i++) {
			product[i] = data1[i] * data2[i];
		}
		return product;
	}

	public static final double[] sumOf(final double[] data1, final double[] data2) {
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("The data arrays must have the same length.");
		}
		final double[] sum = new double[data1.length];
		for (int i = 0; i < data1.length; i++) {
			sum[i] = data1[i] + data2[i];
		}
		return sum;
	}
}
