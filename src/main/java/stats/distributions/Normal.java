package stats.distributions;

import smile.stat.distribution.GaussianDistribution;

public final class Normal implements Distribution {
	
//	private final double mean;
//	private final double stdev;
	private final smile.stat.distribution.Distribution dist;
	
	public Normal(final double mean, final double stdev) {
//		this.mean = mean;
//		this.stdev = stdev;
		this.dist = new GaussianDistribution(mean, stdev);
	}

	@Override
	public final double rand() {
		return this.dist.rand();
	}
	
	@Override
	public final double quantile(final double prob) {
	  return this.dist.quantile(prob);
	}

}
