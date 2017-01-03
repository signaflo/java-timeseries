package timeseries.models.arima;

import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;
import timeseries.models.arima.StateSpaceARMA;

import static org.ejml.ops.CommonOps.*;

/**
 * An implementation of the <a target="_blank"
 * href="https://www.cl.cam.ac.uk/~rmf25/papers/Understanding%20the%20Basis%20of%20the%20Kalman%20Filter.pdf">
 * Kalman Filter</a>, specifically designed for estimation of ARMA models.
 *
 * @author Jacob Rachiele
 *         Date: Dec 09 2016
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
  // elsewhere. Any of its transformations are done manually as documented in the filter method.

  private final KalmanOutput kalmanOutput;

  ArmaKalmanFilter(final StateSpaceARMA ss) {
    this.y = ss.differencedSeries();
    this.r = ss.r();

    this.transitionFunction = new DenseMatrix64F(ss.transitionMatrix());
    final RowD1Matrix64F R = new DenseMatrix64F(r, 1, true, ss.movingAverageVector());
    this.stateDisturbance = new DenseMatrix64F(r, r);
    multOuter(R, stateDisturbance);
    this.predictedState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.filteredState = new DenseMatrix64F(r, 1, true, new double[r]);
    this.predictedStateCovariance = initializePredictedCovariance(ss);
    this.filteredStateCovariance = new DenseMatrix64F(r, r);
    this.predictionErrorVariance = new double[y.length];
    this.predictionError = new double[y.length];
    this.predictedCovarianceFirstColumn = new DenseMatrix64F(r, 1);
    extractColumn(predictedStateCovariance, 0, predictedCovarianceFirstColumn);
    this.kalmanOutput = filter();
    //this.kalmanOutput = filterR(ss);
    //filter();
  }

  double[] predictionError() {
    return this.predictionError.clone();
  }

  double ssq() {
    return this.kalmanOutput.ssq;
  }

  double logLikelihood() {
    return this.kalmanOutput.logLikelihood;
  }

  KalmanOutput output() {
    return this.kalmanOutput;
  }

  private KalmanOutput filter() {

    double f;
    predictionError[0] = y[0];
    // f[t] is always the first element of the first column of the predicted covariance matrix,
    // because f[t] = Z * M, where Z is a row vector with a 1 in the first (index 0) position and zeros elsewhere,
    // and M is the first column of the predicted covariance matrix.
    f = predictionErrorVariance[0] = predictedCovarianceFirstColumn.get(0);

    double ssq = ((predictionError[0] * predictionError[0]) / f);
    double sumlog = Math.log(f);
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

    predictionError[0] /= Math.sqrt(f);


    for (int t = 1; t < y.length; t++) {

      // Update predicted mean of the state vector.
      mult(transitionFunction, filteredState, predictedState);

      // Update predicted covariance of the state vector.
      mult(transitionFunction, filteredStateCovariance, filteredCovarianceTransition);
      mult(filteredCovarianceTransition, transitionTranspose, stateCovarianceTransition);
      add(stateCovarianceTransition, stateDisturbance, predictedStateCovariance);

      predictionError[t] = y[t] - predictedState.get(0);
      extractColumn(predictedStateCovariance, 0, predictedCovarianceFirstColumn);
      f = predictionErrorVariance[t] = predictedCovarianceFirstColumn.get(0);
      ssq += ((predictionError[t] * predictionError[t]) / f);
      sumlog += Math.log(f);

      // Update filteredState.
      newInfo = this.predictedCovarianceFirstColumn.copy();
      scale(predictionError[t], newInfo);
      divide(newInfo, f);
      add(predictedState, newInfo, filteredState);

      // Update filteredCovariance.
      multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
      divide(adjustedPredictionCovariance, f);
      subtract(predictedStateCovariance, adjustedPredictionCovariance, filteredStateCovariance);

      predictionError[t] /= Math.sqrt(f);
    }
    return new KalmanOutput(this.y.length, ssq, sumlog, predictionError);
  }

  private DenseMatrix64F initializePredictedCovariance(final StateSpaceARMA ss) {
    double[] P = getInitialStateCovariance(ss.arParams(), ss.maParams());
    return new DenseMatrix64F(ss.r(), ss.r(), true, unpack(P));
  }

  /**
   * Adapted from <a href="https://www.stat.berkeley.edu/classes/s244/as154.pdf">algorithm AS 154</a>. Some parts of
   * translation confirmed by prior translation to C in
   * <a href="https://github.com/SurajGupta/r-source/blob/master/src/library/stats/src/arima.c#L1009">R's arima.c</a>
   *
   * @param phi   a vector of autoregressive parameters.
   * @param theta a vector of moving-average parameters.
   * @return the initial covariance matrix of the state variables as a lower triangular matrix stored in a 1D-array.
   */
  static double[] getInitialStateCovariance(final double[] phi, final double[] theta) {
    final int p = phi.length;
    final int q = theta.length;
    if (p == 0 && q == 0) {
      return new double[]{1.0};
    }
    final int r = Math.max(p, q + 1);

    // np is the size of the lower triangular part of the symmetric covariance matrix, including the diagonal.
    final int np = r * (r + 1) / 2;
    final double[] P = new double[np];

    // V is R*R', the outer product of the moving-average vector, in lower triangular form.
    final double[] V = new double[np];
//    for (int ind = 0, j = 0; j < r; j++) {
//      double vj = 0.0;
//      if (j == 0) vj = 1.0; else if (j - 1 < q) vj = theta[j - 1];
//      for (int i = j; i < r; i++) {
//        double vi = 0.0;
//        if (i == 0) vi = 1.0; else if (i - 1 < q) vi = theta[i - 1];
//        V[ind++] = vi * vj;
//      }
//    }
    final double[] xrow = new double[np];
    final int nrbar = np * (np - 1) / 2;
    int fault = validate(p, q, r, np, nrbar);
    if (fault != 0) {
      throw new RuntimeException("Validation Error # " + fault);
    }

    for (int i = 1; i < r; i++) {
      V[i] = 0.0;
      if (i <= q) {
        V[i] = theta[i - 1];
      }
    }
    V[0] = 1.0;

    int index = r;
    double vj;
    for (int j = 1; j < r; j++) {
      vj = V[j];
      for (int i = j; i < r; i++) {
        V[index++] = V[i] * vj;
      }
    }

    if (p == 0) {
      // goto 300 (see referenced algorithm).
      int indexn = np;
      index = np;
      for (int i = 0; i < r; i++) {
        for (int j = 0; j <= i; j++) {
          index--;
          P[index] = V[index];
          if (j != 0) {
            P[index] += P[--indexn];
          }
        }
      }
      return P;
    }

    double[] rbar = new double[nrbar];
    double[] thetab = new double[np];
    double[] xnext = new double[np];
    index = 0;
    int index1 = -1;
    int npr = np - r;
    int npr1 = npr + 1;
    int indexj = npr;
    int index2 = npr - 1;
    int indexi;
    double phij, ynext, phii;

    for (int j = 0; j < r; j++) {
      phij = (j < p) ? phi[j] : 0.0;
      xnext[indexj++] = 0.0;
      indexi = npr1 + j;
      for (int i = j; i < r; i++) {
        ynext = V[index++];
        phii = (i < p) ? phi[i] : 0.0;
        if (j != r - 1) {
          xnext[indexj] = -phii;
          if (i != r - 1) {
            xnext[indexi] -= phij;
            xnext[++index1] = -1.0;
          }
        }
        xnext[npr] = -phii * phij;
        index2++;
        if (index2 >= np) {
          index2 = 0;
        }
        xnext[index2] += 1.0;
        inclu2(np, xnext, xrow, ynext, P, rbar, thetab);
        xnext[index2] = 0.0;
        if (i != r - 1) {
          xnext[indexi++] = 0.0;
          xnext[index1] = 0.0;
        }
      }
    }

    regres(np, nrbar, rbar, thetab, P);

    index = npr;
    for (int i = 0; i < r; i++) {
      xnext[i] = P[index++];
    }
    index = np - 1;
    index1 = npr - 1;
    for (int i = 0; i < npr; i++) {
      P[index--] = P[index1--];
    }
    System.arraycopy(xnext, 0, P, 0, r);
    return P;
  }

  private static int validate(int ip, int iq, int ir, int np, int nrbar) {
    if (ip == 0 && iq == 0) {
      return 4;
    }
    if (np != ir * (ir + 1) / 2) {
      return 6;
    }
    if (nrbar != np * (np - 1) / 2) {
      return 7;
    }
    return 0;
  }

  private static void inclu2(final int np, final double[] xnext, final double[] xrow,
                             final double ynext, final double[] d, final double[] rbar, final double[] thetab) {

    double xi, di, dpi, cbar, sbar, xk, rbthis;
    System.arraycopy(xnext, 0, xrow, 0, np);
    int ithisr = 0;
    double y = ynext;
    double wt = 1.0;
    for (int i = 0; i < np; i++) {
      if (xrow[i] != 0.0) {
        xi = xrow[i];
        di = d[i];
        dpi = di + wt * xi * xi;
        d[i] = dpi;
        cbar = di / dpi;
        sbar = wt * xi / dpi;
        wt = cbar * wt;
        if (i != np - 1) {
          int i1 = i + 1;
          for (int k = i1; k < np; k++) {
            xk = xrow[k];
            rbthis = rbar[ithisr];
            xrow[k] = xk - xi * rbthis;
            rbar[ithisr++] = cbar * rbthis + sbar * xk;
          }
        }
        xk = y;
        y = xk - xi * thetab[i];
        thetab[i] = cbar * thetab[i] + sbar * xk;
        if (di == 0.0) {
          return;
        }
      } else {
        ithisr = ithisr + np - i - 1;
      }
    }


  }

  private static void regres(final int np, final int nrbar, final double[] rbar, final double[] thetab, final
  double[] beta) {
    int ithisr = nrbar - 1;
    int im = np - 1;
    double bi;
    int i1;
    int jm;
    for (int i = 0; i < np; i++) {
      bi = thetab[im];
      if (im != np - 1) {
        i1 = i;
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

//  private static void karma(final int ip, final int iq, final int ir, final int np, final double[] phi, final
//  double[] theta,
//                            final double[] a, final double[] p, final double[] v, final int n, final double[] w,
//                            final double[] resid, double sumlog, double ssq, final int iupd,
//                            final double delta, int nit) {
//    int ir1 = ir - 1;
//    final double[] e = new double[ir];
//    int inde = 0;
//
//    if (nit != 0) {
//      goto600(n, w, ip, iq, phi, theta, inde, e, resid, ssq);
//      double wnext;
//      double dt;
//      for (int i = 0; i < n; i++) {
//        wnext = w[i];
//        if (iupd != 1 || i != 0) {
//          dt = 0.0;
//        }
//      }
//    }
//  }
//
//  private static void goto600(int n, double[] w, int ip, int iq, double[] phi, double[] theta, int inde, double[] e,
//                              double
//                                  [] resid, double ssq) {
//    int i = 0;
//    double et;
//    int indw;
//    for (int ii = i; ii < n; ii++) {
//      et = w[ii];
//      indw = ii;
//      if (ip != 0) {
//        for (int j = 0; j < ip; j++) {
//          indw--;
//          if (indw >= 0) {
//            et -= (phi[j] * w[indw]);
//          } else {
//            break;
//          }
//        }
//      }
//      if (iq != 0) {
//        for (int j = 0; j < iq; j++) {
//          if (--inde == -1) {
//            inde = iq - 1;
//          }
//          et -= (theta[j] * e[inde]);
//        }
//      }
//      e[inde++] = et;
//      resid[ii] = et;
//      ssq += et * et;
//      if (inde >= iq) {
//        inde = 0;
//      }
//    }
//  }
//
//  private void kalform(final int m, final int ip, final int ir, final int np, final double[] phi, final double[] a,
//                       final double[] p, final double[] v, final double[] work) {
//
//  }
//
  static double[] unpack(final double[] triangularMatrix) {
    int c = triangularMatrix.length;
    //x^2 + x - 2c = 0
    int r = (-1 + (int) Math.sqrt(1 + 4 * 2 * c)) / 2;
    double[] full = new double[r * r];
    int k = 0;
    int indext = 0;
    for (int i = 0; i < r; i++, k++) {
      for (int j = 0; j < r - k; j++) {
        full[j + k + i * r] = triangularMatrix[indext++];
      }
    }
    for (int i = 0; i < r - 1; i++) {
      for (int j = i + 1; j < r; j++) {
        full[i + r * j] = full[j + i * r];
      }
    }
    return full;
  }

  static class KalmanOutput {

    private final double ssq;
    private final double sumlog;
    private final double sigma2ML;
    private final double logLikelihood;
    private final double[] residuals;

    KalmanOutput(final int n, final double ssq, final double sumlog, final double[] residuals) {
      this.ssq = ssq;
      this.sumlog = sumlog;
      this.sigma2ML = ssq / n;
      this.logLikelihood = -0.5 * (n * (Math.log(sigma2ML) + Math.log(2 * Math.PI) + 1.0) + sumlog);
      this.residuals = residuals.clone();
    }

    double ssq() {
      return this.ssq;
    }

    double sumLog() {
      return this.sumlog;
    }

    double sigma2() {
      return this.sigma2ML;
    }

    double logLikelihood() {
      return this.logLikelihood;
    }

    double[] residuals() {
      return this.residuals.clone();
    }

  }

//  KalmanOutput filterR(StateSpaceARMA ss) {
//    boolean useResid = true;
//    double[] y = ss.differencedSeries();
//    double[] rsResid = new double[y.length];
//    double[] phi = ss.arParams();
//    double[] theta = ss.maParams();
//    double sumlog = 0.0, ssq = 0;
//    double[] anew, M, a, Pnew, P;
//    int rd = r;
//    int n = y.length;
//    int p = phi.length;
//    int q = theta.length;
//    Pnew = unpack(getInitialStateCovariance(ss.arParams(), ss.maParams()));
//    P = new double[Pnew.length];
//    a = new double[rd];
//    anew = new double[rd];
//    M = new double[rd];
//    int nu = 0;
//    for (int l = 0; l < n; l++) {
//      if (l > 0) {
//        for (int i = 0; i < r; i++) {
//          double tmp = (i < r - 1) ? a[i + 1] : 0.0;
//          if (i < p) {
//            tmp += phi[i] * a[0];
//          }
//          anew[i] = tmp;
//        }
//        for (int i = 0; i < r; i++) {
//          double vi = 0.0;
//          if (i == 0) {
//            vi = 1.0;
//          } else if (i - 1 < q) {
//            vi = theta[i - 1];
//          }
//          for (int j = 0; j < r; j++) {
//            double tmp = 0.0;
//            if (j == 0) {
//              tmp = vi;
//            } else if (j - 1 < q) {
//              tmp = vi * theta[j - 1];
//            }
//            if (i < p && j < p) {
//              tmp += phi[i] * phi[j] * P[0];
//            }
//            if (i < r - 1 && j < r - 1) {
//              tmp += P[i + 1 + r * (j + 1)];
//            }
//            if (i < p && j < r - 1) {
//              tmp += phi[i] * P[j + 1];
//            }
//            if (j < p && i < r - 1) {
//              tmp += phi[j] * P[i + 1];
//            }
//            Pnew[i + r * j] = tmp;
//          }
//        }
//      }
//      if (!Double.isNaN(y[l])) {
//        double resid = y[l] - anew[0];
//
//        System.arraycopy(Pnew, 0, M, 0, rd);
//
//        double gain = M[0];
//        if (gain < 1e4) {
//          nu++;
//          ssq += resid * resid / gain;
//          sumlog += Math.log(gain);
//        }
//        rsResid[l] = resid / Math.sqrt(gain);
//        for (int i = 0; i < rd; i++)
//          a[i] = anew[i] + M[i] * resid / gain;
//        for (int i = 0; i < rd; i++)
//          for (int j = 0; j < rd; j++)
//            P[i + j * rd] = Pnew[i + j * rd] - M[i] * M[j] / gain;
//      } else {
//        System.arraycopy(anew, 0, a, 0, rd);
//        System.arraycopy(Pnew, 0, P, 0, rd * rd);
//        rsResid[l] = Double.NaN;
//      }
//    }
//    return new KalmanOutput(n, ssq, sumlog, rsResid);
//  }

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
//    this.m = ss.r();
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
