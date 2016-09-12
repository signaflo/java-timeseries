package timeseries.models;

import org.ejml.data.DenseMatrix64F;
import static org.ejml.ops.CommonOps.*;

import timeseries.models.arima.StateSpaceARMA;

public final class KalmanFilter {
  
  private final double[] y;
  private final int r;
  private final DenseMatrix64F Z;
  private final DenseMatrix64F T;
  private final DenseMatrix64F V;
  private final DenseMatrix64F predictedState;
  private final DenseMatrix64F filteredState;
  private final DenseMatrix64F predictedCovariance;
  private final DenseMatrix64F filteredCovariance;
  private final double[] f;
  private final double[] v;
  private final DenseMatrix64F M; // Note that M is the first column of predictedCovariance;
  
  KalmanFilter(final StateSpaceARMA ss) {
    this.y = ss.differencedSeries();
    this.r = ss.r();
    this.Z = new DenseMatrix64F(1, r, true, ss.stateEffectsVector());
    this.predictedState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.filteredState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.predictedCovariance = identity(r);
    this.filteredCovariance = identity(r);
    this.T = new DenseMatrix64F(ss.transitionMatrix());
    DenseMatrix64F R = new DenseMatrix64F(r, 1, true, ss.movingAverageVector());
    this.V = new DenseMatrix64F(r, r);
    multOuter(R, V);
    this.f = new double[y.length];
    this.v = new double[y.length];
    double[] mArray = new double[r];
    mArray[0] = 1.0;
    this.M = new DenseMatrix64F(r, 1, true, mArray); 
  }
  
  public final void filter() {
    v[0] = y[0];
    f[0] = 1.0; // f[t] is always the first element of column vector M;
    
    // Initialize filteredState.
    DenseMatrix64F Mvf = this.M.copy();
    scale(v[0], Mvf);
    divide(Mvf, f[0]);
    add(predictedState, Mvf, filteredState);
    
    // Initialize filteredCovariance.
    final DenseMatrix64F MMf = new DenseMatrix64F(r, r);
    multOuter(M, MMf);
    divide(MMf, f[0]);
    subtract(predictedCovariance, MMf, filteredCovariance);
    
    DenseMatrix64F TP = new DenseMatrix64F(r, r);
    DenseMatrix64F TPT = new DenseMatrix64F(r, r);
    DenseMatrix64F Ttranspose = T.copy();
    transpose(Ttranspose);
    for (int t = 1; t < y.length; t++) {
      
      // Update predicted state.
      mult(T, filteredState, predictedState);
      
      // Update predicted covariance.
      mult(T, filteredCovariance, TP);
      mult(TP, Ttranspose, TPT);
      add(TPT, V, predictedCovariance);
      
      v[t] = y[t] - predictedState.get(0);
      extractColumn(predictedCovariance, 0, M);
      f[t] = M.get(0);
      
      // Update filteredState.
      Mvf = this.M.copy();
      scale(v[t], Mvf);
      divide(Mvf, f[t]);
      add(predictedState, Mvf, filteredState);
      
      // Update filteredCovariance.
      multOuter(M, MMf);
      divide(MMf, f[t]);
      subtract(predictedCovariance, MMf, filteredCovariance);
    }
  }
  
//  private final double[] series;
//  private final double[] initialStateVector;
//  private final double[] arParams;
//  private final double[] maParams;
//  private final int m;
//  private final double[][] V;
//  private final double[] v;
//  private final double[][] T;
//  private final double[][] initialCovariance;
//  private final double[][] filteredCovariance;
//  private final double[] filteredState;
//  private final double[] f;
//  private final double[] K;
//  
//  public KalmanFilter(final StateSpaceARMA ss) {
//    this.series = ss.differencedSeries();
//    this.arParams = ss.arParams();
//    this.maParams = ss.maParams();
//    this.m = ss.m();
//    this.initialStateVector = new double[m];
//    this.filteredState = new double[m];
//    this.V = ss.V();
//    this.v = new double[series.length];
//    this.T = ss.F();
//    this.initialCovariance = new double[m][m];
//    this.filteredCovariance = new double[m][m];
//    this.f = new double[series.length];
//    this.K = new double[series.length];
//  }
//  
//  public void primFilter() {
//    double[] stateVector = initialStateVector.clone();
//    double[][] covariance = initialCovariance.clone();
//    v[0] = series[0];
//    // F at time t is the element of P at (0, 0) + m.
//    f[0] = covariance[0][0] + m;
//    
//    double[] M = new double[m];
//    for (int i = 0; i < m; i++) {
//      M[i] = covariance[0][i];
//    }
//    
//    for (int i = 0; i < m; i++) {
//      for (int j = 0; j < m; j++) {
//        filteredCovariance[i][j] = covariance[i][j] - (M[i] * M[j])/f[0];
//      }
//    }
//    
//    for (int i = 0; i < m; i++) {
//      filteredState[i] = stateVector[i] + M[i] * v[0] / f[0];
//    } 
//  }

}
