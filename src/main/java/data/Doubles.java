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
	
	public static final double[] slice(final double[] data, final int from, final int to) {
		final double[] sliced = new double[to - from];
		System.arraycopy(data, from, sliced, 0, to - from);
		return sliced;
	}
}
