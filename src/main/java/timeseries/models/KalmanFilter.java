package timeseries.models;

import static org.ejml.ops.CommonOps.add;
import static org.ejml.ops.CommonOps.divide;
import static org.ejml.ops.CommonOps.extractColumn;
import static org.ejml.ops.CommonOps.fill;
import static org.ejml.ops.CommonOps.identity;
import static org.ejml.ops.CommonOps.invert;
import static org.ejml.ops.CommonOps.kron;
import static org.ejml.ops.CommonOps.mult;
import static org.ejml.ops.CommonOps.multOuter;
import static org.ejml.ops.CommonOps.scale;
import static org.ejml.ops.CommonOps.subtract;
import static org.ejml.ops.CommonOps.transpose;

import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;
import org.ejml.equation.Equation;

import timeseries.models.arima.StateSpaceARMA;

public final class KalmanFilter {
  
  private final double[] y;
  private final int r; // r = max(p, q + 1);
  private final DenseMatrix64F transitionFunction;
  private final RowD1Matrix64F stateDisturbance;
  private final RowD1Matrix64F predictedState;
  private final RowD1Matrix64F filteredState;
  private final DenseMatrix64F stateCovariancePrediction;
  private final RowD1Matrix64F filteredStateCovariance;
  private final double[] predictionErrorVariance;
  private final double[] predictionError;
  //M is the first column of the predictedCovariance matrix.
  private final DenseMatrix64F predictedCovarianceFirstColumn; 
  // We don't include Z. It is a row vector with a 1 in the first position and zeros
  // elsewhere. Any of its transformations are done manually as documented below.
  
  KalmanFilter(final StateSpaceARMA ss) {
    this.y = ss.differencedSeries();
    this.r = ss.r();
    this.transitionFunction = new DenseMatrix64F(ss.transitionMatrix());
    final RowD1Matrix64F R = new DenseMatrix64F(r, 1, true, ss.movingAverageVector());
    this.stateDisturbance = new DenseMatrix64F(r, r);
    multOuter(R, stateDisturbance);
    this.predictedState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.filteredState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.stateCovariancePrediction = initializePredictedCovariance();
    this.filteredStateCovariance = new DenseMatrix64F(r, r);
    this.predictionErrorVariance = new double[y.length];
    this.predictionError = new double[y.length];
    double[] mArray = new double[r];
    mArray[0] = 1.0;
    this.predictedCovarianceFirstColumn = new DenseMatrix64F(r, 1, true, mArray);
    filter();
  }
  
  private final DenseMatrix64F initializePredictedCovariance() {
    final DenseMatrix64F P = new DenseMatrix64F(r * r, 1);
    final RowD1Matrix64F id = identity(r * r);
    final DenseMatrix64F kronT = new DenseMatrix64F(r * r, r * r);
    kron(transitionFunction, transitionFunction, kronT);
    final DenseMatrix64F idKronT = new DenseMatrix64F(r * r, r * r);
    subtract(id, kronT, idKronT);
    final DenseMatrix64F RQR = this.stateDisturbance.copy();
    RQR.reshape(r * r, 1);
    final boolean solved = invert(idKronT);
    if (solved) {
      mult(idKronT, RQR, P);
    }
    else {
      fill(P, 1.0);
    }
    P.reshape(r, r);
    return P;
  }

  private final void filter() {
    
    predictionError[0] = y[0];
    // f[t] is always the first element of column vector M because f[t] = Z*M, where
    // Z is a row vector with a 1 in the first (index 0) position and zeros elsewhere.
    predictionErrorVariance[0] = predictedCovarianceFirstColumn.get(0);
    
    // Initialize filteredState.
    RowD1Matrix64F newInfo = this.predictedCovarianceFirstColumn.copy();
    scale(predictionError[0], newInfo);
    divide(newInfo, predictionErrorVariance[0]);
    add(predictedState, newInfo, filteredState);
    
    // Initialize filteredCovariance.
    final RowD1Matrix64F adjustedPredictionCovariance = new DenseMatrix64F(r, r);
    multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
    divide(adjustedPredictionCovariance, predictionErrorVariance[0]);
    subtract(stateCovariancePrediction, adjustedPredictionCovariance, filteredStateCovariance);
    
    final RowD1Matrix64F filteredCovarianceTransition = new DenseMatrix64F(r, r);
    final RowD1Matrix64F stateCovarianceTransition = new DenseMatrix64F(r, r);
    final DenseMatrix64F Ttranspose = transitionFunction.copy();
    transpose(Ttranspose);
    
    
    for (int t = 1; t < y.length; t++) {
      
      // Update predicted mean of the state vector.
      mult(transitionFunction, filteredState, predictedState);
      
      // Update predicted covariance of the state vector.
      mult(transitionFunction, filteredStateCovariance, filteredCovarianceTransition);
      mult(filteredCovarianceTransition, Ttranspose, stateCovarianceTransition);
      add(stateCovarianceTransition, stateDisturbance, stateCovariancePrediction);
      
      predictionError[t] = y[t] - predictedState.get(0);
      extractColumn(stateCovariancePrediction, 0, predictedCovarianceFirstColumn);
      predictionErrorVariance[t] = predictedCovarianceFirstColumn.get(0);
      
      // Update filteredState.
      newInfo = this.predictedCovarianceFirstColumn.copy();
      scale(predictionError[t], newInfo);
      divide(newInfo, predictionErrorVariance[t]);
      add(predictedState, newInfo, filteredState);
      
      // Update filteredCovariance.
      multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
      divide(adjustedPredictionCovariance, predictionErrorVariance[t]);
      subtract(stateCovariancePrediction, adjustedPredictionCovariance, filteredStateCovariance);
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
