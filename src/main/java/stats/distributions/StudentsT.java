package stats.distributions;

import smile.stat.distribution.TDistribution;

public final class StudentsT implements Distribution {
  
  private final TDistribution dist;
  
  public StudentsT(final int df) {
    this.dist = new TDistribution(df);
  }

  @Override
  public double rand() {
    return this.dist.rand();
  }
  
  @Override
  public final double quantile(final double prob) {
    return this.dist.quantile(prob);
  }

}
