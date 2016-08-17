package stats.distributions;

import smile.stat.distribution.GaussianDistribution;

public final class NormalDistribution implements Distribution {
	
//	private final double mean;
//	private final double stdev;
	private final smile.stat.distribution.Distribution dist;
	
	public NormalDistribution(final double mean, final double stdev) {
//		this.mean = mean;
//		this.stdev = stdev;
		this.dist = new GaussianDistribution(mean, stdev);
	}

	@Override
	public final double rand() {
		return this.dist.rand();
	}

}
