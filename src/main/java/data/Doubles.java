package data;

public final class Doubles {

	private Doubles(){}
	
	public static final double[] fill(final int size, final double value) {
		final double[] filled = new double[size];
		for (int i = 0; i < filled.length; i++) {
			filled[i] = value;
		}
		return filled;
	}
}
