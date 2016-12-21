package timeseries.models;

import static org.ejml.ops.CommonOps.*;

import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;

import timeseries.models.arima.StateSpaceARMA;

/**
 * An implementation of the <a target="_blank"
 * href="https://www.cl.cam.ac.uk/~rmf25/papers/Understanding%20the%20Basis%20of%20the%20Kalman%20Filter.pdf">
 * Kalman Filter specifically designed for estimation of ARMA models.
 *
 *
 * @author Jacob Rachiele
 * Date: Dec 09 2016
 *
 * TODO: Make faster by initializing prediction covariance without explicit inversion of large matrix.
 *
 */
final class ArmaKalmanFilter {
  
  private final double[] y;
  private final int r; // r = max(p, q + 1);
  private final DenseMatrix64F transitionFunction;
  private final RowD1Matrix64F stateDisturbance;
  private final RowD1Matrix64F predictedState;
  private final RowD1Matrix64F filteredState;
  private final DenseMatrix64F predictedStateCovariance;
  private final RowD1Matrix64F filteredStateCovariance;
  private final double[] predictionErrorVariance;
  private final double[] predictionError;
  // the following is the first column of the predictedCovariance matrix.
  private final DenseMatrix64F predictedCovarianceFirstColumn; 
  // We don't include Z. It is a row vector with a 1 in the first position and zeros
  // elsewhere. Any of its transformations are done manually as documented below.
  
  ArmaKalmanFilter(final StateSpaceARMA ss) {
    this.y = ss.differencedSeries();
    this.r = ss.r();
    this.transitionFunction = new DenseMatrix64F(ss.transitionMatrix());
    final RowD1Matrix64F R = new DenseMatrix64F(r, 1, true, ss.movingAverageVector());
    this.stateDisturbance = new DenseMatrix64F(r, r);
    multOuter(R, stateDisturbance);
    this.predictedState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.filteredState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.predictedStateCovariance = initializePredictedCovariance();
    this.filteredStateCovariance = new DenseMatrix64F(r, r);
    this.predictionErrorVariance = new double[y.length];
    this.predictionError = new double[y.length];
    this.predictedCovarianceFirstColumn = new DenseMatrix64F(r, 1);
    extractColumn(predictedStateCovariance, 0, predictedCovarianceFirstColumn);
    filter();
  }
  
  private DenseMatrix64F initializePredictedCovariance() {
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

  private void filter() {
    
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
    subtract(predictedStateCovariance, adjustedPredictionCovariance, filteredStateCovariance);
    
    final RowD1Matrix64F filteredCovarianceTransition = new DenseMatrix64F(r, r);
    final RowD1Matrix64F stateCovarianceTransition = new DenseMatrix64F(r, r);
    final DenseMatrix64F transitionTranspose = transitionFunction.copy();
    transpose(transitionTranspose);
    
    
    for (int t = 1; t < y.length; t++) {
      
      // Update predicted mean of the state vector.
      mult(transitionFunction, filteredState, predictedState);
      
      // Update predicted covariance of the state vector.
      mult(transitionFunction, filteredStateCovariance, filteredCovarianceTransition);
      mult(filteredCovarianceTransition, transitionTranspose, stateCovarianceTransition);
      add(stateCovarianceTransition, stateDisturbance, predictedStateCovariance);
      
      predictionError[t] = y[t] - predictedState.get(0);
      extractColumn(predictedStateCovariance, 0, predictedCovarianceFirstColumn);
      predictionErrorVariance[t] = predictedCovarianceFirstColumn.get(0);
      
      // Update filteredState.
      newInfo = this.predictedCovarianceFirstColumn.copy();
      scale(predictionError[t], newInfo);
      divide(newInfo, predictionErrorVariance[t]);
      add(predictedState, newInfo, filteredState);
      
      // Update filteredCovariance.
      multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
      divide(adjustedPredictionCovariance, predictionErrorVariance[t]);
      subtract(predictedStateCovariance, adjustedPredictionCovariance, filteredStateCovariance);
    }
  }

  static double[] starma(final double[] phi, final double[] theta) {
    final int ip = phi.length;
    final int iq = theta.length;
    final int ir = Math.max(ip, iq + 1);
    final int np = ir * (ir + 1) / 2;
    final double[] p = new double[np];
    final double[] v = new double[np];
    final double[] xrow = new double[np];
    final int nrbar = np * (np - 1) / 2;
    int ifault = validate(ip, iq, ir, np, nrbar);
    if (ifault != 0) {
      throw new RuntimeException("validation error");
    }

    for (int i = 1; i < ir; i++) {
      v[i] = 0.0;
      if (i <= iq) {
        v[i] = theta[i - 1];
      }
    }
    v[0] = 1.0;

    int ind = ir;
    double vj;
    for (int j = 1; j < ir; j++) {
      vj = v[j];
      for (int i = j; i < ir; i++) {
        v[ind++] = v[i] * vj;
      }
    }

    if (ip == 0) {
      // goto 300
      int indn = np;
      ind = np;
      for (int i = 0; i < ir; i++) {
        for (int j = 0; j <= i; j++) {
          ind--;
          p[ind] = v[ind];
          if (j != 0) {
            indn--;
            p[ind] += p[indn];
          }
        }
      }
    } else {
      int ir1 = ir - 1;
      int irank = 0;
      int ifail = 0;
      double ssqerr = 0;
      double[] rbar = new double[nrbar];
      double[] thetab = new double[np];
      double[] xnext = new double[np];
      ind = 0;
      int ind1 = -1;
      int npr = np - ir;
      int npr1 = npr + 1;
      int indj = npr;
      int ind2 = npr - 1;
      double phij;
      double ynext;
      double phii;
      int indi;

      for (int j = 0; j < ir; j++) {
        phij = (j < ip)? phi[j] : 0.0;
        indj++;
        indi = npr1 + j;
        for (int i = j; i < ir; i++) {
          ynext = v[ind++];
          phii = (i < ip)? phi[i] : 0.0;
          if (j + 1 == ir) {
            xnext[npr1] = -phii * phij;
            ind2++;
            if (ind2 >= np) {
              ind2 = 0;
            }
            xnext[ind2] += 1.0;
            inclu2(np, nrbar, 1.0, xnext, xrow, ynext, p, rbar, thetab);
            xnext[ind2] = 0.0;
            if (i != ir - 1) {
              xnext[indi++] = 0.0;
              xnext[ind1] = 0.0;
            }
          }
        }
      }

      regres(np, nrbar, rbar, thetab, p);

      ind = npr - 1;
      for (int i = 0; i < ir; i++) {
        xnext[i] = p[++ind];
      }
      ind = np - 1;
      ind1 = npr - 1;
      for (int i = 0; i < npr; i++) {
        p[ind--] = p[ind1--];
      }
      for (int i = 0; i < ir; i++) {
        p[i] = xnext[i];
      }
    }
    return p;

  }

  private static int validate(int ip, int iq, int ir, int np, int nrbar) {
    if (ip < 0) {
      return 1;
    }
    if (iq < 0) {
      return 2;
    }
    if (ip < 0 && iq < 0) {
      return 3;
    }
    if (ip == 0 && iq == 0) {
      return 4;
    }
    if (ir != Math.max(ip, iq + 1)) {
      return 5;
    }
    if (np != ir * (ir + 1) / 2) {
      return 6;
    }
    if (nrbar != np * (np - 1) / 2) {
      return 7;
    }
    if (ip == 1 && iq == 0) {
      return 8;
    }
    return 0;
  }

  private void karma(final int ip, final int iq, final int ir, final int np, final double[] phi, final double[] theta,
                     final double[] a, final double[] p, final double[] v, final int n, final double[] w,
                     final double[] resid, double sumlog, double ssq, final int iupd,
                     final double delta, final double[] e, final int nit) {

  }

  private void kalform(final int m, final int ip, final int ir, final int np, final double[] phi, final double[] a,
                       final double[] p, final double[] v, final double[] work) {

  }

  private static int inclu2(final int np, final int nrbar, final double weight, final double[] xnext, final double[] xrow,
                      final double ynext, final double[] d, final double[] rbar, final double[] thetab) {

    int irank = 0;
    double y = ynext;
    double wt = weight;
    double xi, di, dpi, cbar, sbar, xk, rbthis;
    double recres = 0.0;
    double ssqerr = 0.0;
    for (int i = 0; i < np; i++) {
      xrow[i] = xnext[i];
    }
    if (wt <= 0) {
      return 1;
    }
    int ithisr = 0;
    for (int i = 0; i < np; i++) {
      if (Math.abs(xrow[i]) > 1E-12) {
        xi = xrow[i];
        di = d[i];
        dpi = di + wt * xi * xi;
        d[i] = dpi;
        cbar = di/dpi;
        sbar = wt * xi / dpi;
        wt = cbar * wt;
          int i1 = i + 1;
          for (int k = i1; k < np; k++) {
            xk = xrow[k];
            rbthis = rbar[ithisr];
            xrow[k] = xk - xi * rbthis;
            rbar[ithisr++] = cbar * rbthis + sbar * xk;
          }
        xk = y;
        y = xk - xi * thetab[i];
        thetab[i] = cbar * thetab[i] + sbar * xk;
        if (Math.abs(di) < 1E-12) {
          return 0;
        }
      }
      else {
        ithisr = ithisr + np - i - 1;
      }
    }
    ssqerr += (wt * y * y);
    recres = y * Math.sqrt(wt);
    return 0;



  }
  private static void regres(final int np, final int nrbar, final double[] rbar, final double[] thetab, final double[] beta) {
    int ithisr = nrbar - 1;
    int im = np - 1;
    double bi;
    double i1;
    int jm;
    for (int i = 0; i < np; i++) {
      bi = thetab[im];
      if (im != np - 1) {
        i1 = i - 2;
        jm = np - 1;
        for (int j = 0; j < i1; j++) {
          bi = bi - rbar[ithisr] * beta[jm];
          ithisr--;
          jm--;
        }
      }
      beta[im] = bi;
      im--;
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
//  public ArmaKalmanFilter(final StateSpaceARMA ss) {
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
