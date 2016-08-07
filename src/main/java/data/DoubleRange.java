package data;

final class DoubleRange {
	
	private final double[] range;
	
	private DoubleRange(final int from, final int to, final int by) {
		range = new double[to - from];
		for (int i = 0; i < range.length; i++) {
			range[i] = from + i * by;
		}
	}
	
	static final DoubleRange newRangeExclusive(final int from, final int to) {
		return new DoubleRange(from, to, 1);
	}
	
	static final DoubleRange newRangeInclusive(final int from, final int to) {
		return new DoubleRange(from, to + 1, 1);
	}
	
	final double[] asArray() {
		return this.range;
	}

	final double sum() {
		return Statistics.sumOf(range);
	}
}
