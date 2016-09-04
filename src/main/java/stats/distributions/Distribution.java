package stats.distributions;

public interface Distribution {
	
	double rand();
	
	double quantile(double prob);

}
