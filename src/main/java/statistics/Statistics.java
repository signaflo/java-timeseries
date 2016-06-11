package statistics;

import static math.Operators.productOf;

public final class Statistics {

	public static final double sumOf(final double[] data) {
		double sum = 0.0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum;
	}

	public static final double meanOf(final double[] data) {
		final double sum = sumOf(data);
		return sum/data.length;
	}

	public static final double varianceOf(final double[] data) {
		final int n = data.length;
		return sumOfSquaredDifferences(data, meanOf(data))/(n - 1);
	}

	public static final double stdDeviationOf(final double[] data) {
		return Math.sqrt(varianceOf(data));
	}
	public static final double sumOfSquared(final double[] data) {
		return sumOf(squared(data));
	}
	
	public static final double sumOfSquaredDifferences(final double[] data, final double point) {
		return sumOf(squared(differences(data, point)));
	}
	
	public static final double[] squared(final double[] data) {
		final double[] squared = new double[data.length];
		for (int i = 0; i < squared.length; i++) {
			squared[i] = data[i]*data[i];
		}
		return squared;
	}
	
	public static final double[] differences(final double[] data, final double point) {
		final double[] differenced = new double[data.length];
		for (int i = 0; i < differenced.length; i++) {
			differenced[i] = data[i] - point;
		}
		return differenced;
	}

	public static final double covariance(double[] data, double[] data2) {
		return sumOf(productOf(differences(data, meanOf(data)),
				differences(data2, meanOf(data2))))/(data.length-1);
	}

}
