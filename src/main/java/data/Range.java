package data;


final class Range {
	
	private final int[] range;
	
	private Range(final int from, final int to, final int by) {
		range = new int[to - from];
		for (int i = 0; i < range.length; i++) {
			range[i] = from + i * by;
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

}
