package timeseries.models;

import timeseries.models.arima.StateSpaceARMA;

public final class KalmanFilter {
  
  private final double[] series;
  private final double[] initialStateVector;
  private final double[] arParams;
  private final double[] maParams;
  private final int m;
  private final double[][] V;
  private final double[] v;
  private final double[][] T;
  private final double[][] initialCovariance;
  private final double[][] filteredCovariance;
  private final double[] filteredState;
  private final double[] f;
  private final double[] K;
  
  public KalmanFilter(final StateSpaceARMA ss) {
    this.series = ss.differencedSeries();
    this.arParams = ss.arParams();
    this.maParams = ss.maParams();
    this.m = ss.m();
    this.initialStateVector = new double[m];
    this.filteredState = new double[m];
    this.V = ss.V();
    this.v = new double[series.length];
    this.T = ss.F();
    this.initialCovariance = new double[m][m];
    this.filteredCovariance = new double[m][m];
    this.f = new double[series.length];
    this.K = new double[series.length];
  }
  
  public void primFilter() {
    double[] stateVector = initialStateVector.clone();
    double[][] covariance = initialCovariance.clone();
    v[0] = series[0];
    // F at time t is the element of P at (0, 0) + m.
    f[0] = covariance[0][0] + m;
    
    double[] M = new double[m];
    for (int i = 0; i < m; i++) {
      M[i] = covariance[0][i];
    }
    
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m; j++) {
        filteredCovariance[i][j] = covariance[i][j] - (M[i] * M[j])/f[0];
      }
    }
    
    for (int i = 0; i < m; i++) {
      filteredState[i] = stateVector[i] + M[i] * v[0] / f[0];
    }
    
    
    
  }

}
