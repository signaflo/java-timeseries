package data;

import statistics.Statistics;

final class Range {
	
	private final int[] range;
	private final double[] data;
	
	private Range(final int from, final int to, final int by) {
		this.range = new int[to - from];
		this.data = new double[to - from];
		for (int i = 0; i < range.length; i++) {
			range[i] = from + i * by;
			data[i] = from + i *by;
		}
	}
	
	public static final Range newRangeExclusive(final int from, final int to) {
		return new Range(from, to, 1);
	}
	
	public static final Range newRangeInclusive(final int from, final int to) {
		return new Range(from, to + 1, 1);
	}
	
	public final int[] asArray() {
		return this.range;
	}
	
	public final double sum() {
		return Statistics.sumOf(data);
	}
	
	public Range sliceInclusive(final int from, final int to) {
		return new Range(this.range[from], this.range[to + 1], 1);
	}
	
	public Range sliceExclusive(final int from, final int to) {
		return new Range(this.range[from], this.range[to], 1);
	}

}
