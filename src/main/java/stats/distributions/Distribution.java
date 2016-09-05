package stats.distributions;

/**
 * Represents a probability distribution.
 * @author Jacob Rachiele
 *
 */
public interface Distribution {
	
  /**
   * Generate a random value from the distribution.
   * @return a random value from the distribution.
   */
	double rand();
	
	/**
	 * Gives the value of the quantile function at the given probability. See <a target="_blank"
	 * href="https://en.wikipedia.org/wiki/Quantile_function">quantile function</a>.
	 * @param prob the probability that the random variable X &le; q, where q is the quantile value.
	 * @return the value of the quantile function at the given probability.
	 */
	double quantile(double prob);

}
