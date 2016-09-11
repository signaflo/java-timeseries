package timeseries.models;

import timeseries.models.arima.StateSpaceARMA;

public final class InformationFilter {
  
  private final double[] series;
  private final double[] initialStateVector;
  private final double[] arParams;
  private final double[] maParams;
  private final int m;
  private final double[] g;
  private final double[] predictionErrors;
  private final double[][] initialCovariance;
  private final double[] Gain;
  private final double[] K;
  
  public InformationFilter(final StateSpaceARMA ss) {
    this.series = ss.differencedSeries();
    this.arParams = ss.arParams();
    this.maParams = ss.maParams();
    this.m = ss.m();
    this.initialStateVector = new double[m];
    this.g = ss.g();
    this.predictionErrors = new double[series.length];
    this.initialCovariance = new double[m][m];
    this.Gain = new double[series.length];
    this.K = new double[series.length];
  }
  
  public void filter() {
    double[] stateVector = initialStateVector.clone();
    double[][] covariance = initialCovariance.clone();
    predictionErrors[0] = series[0];
    // F at time t is the element of P at (0, 0) + m.
    Gain[0] = covariance[0][0] + m;
    
  }

}
