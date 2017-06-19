package timeseries.models.arima;

import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;

import static java.lang.Math.PI;
import static java.lang.Math.log;
import static org.ejml.ops.CommonOps.*;

class ArimaKalmanFilter {

    private final double[] y;
    private final int r; // r = max(p, q + 1);
    private final int d;
    private final int rd;
    private final DenseMatrix64F transitionMatrix;
    private final RowD1Matrix64F stateDisturbance;
    private final RowD1Matrix64F predictedState;
    private final RowD1Matrix64F filteredState;
    private final DenseMatrix64F predictedStateCovariance;
    private final RowD1Matrix64F filteredStateCovariance;
    private final double[] predictionErrorVariance;
    private final double[] predictionError;

    private final DenseMatrix64F Z;
    private final DenseMatrix64F Zt;
    private final DenseMatrix64F ZP;
    private DenseMatrix64F PZtf;
    private final DenseMatrix64F PZtfZ;
    private final KalmanOutput kalmanOutput;

    ArimaKalmanFilter(final StateSpaceARIMA ss) {
        this.y = ss.observations();
        this.r = ss.r();
        this.d = ss.d();
        this.rd = r + d;

        this.transitionMatrix = new DenseMatrix64F(ss.transitionMatrix());
        final RowD1Matrix64F R = new DenseMatrix64F(rd, 1, true, ss.movingAverageVector());
        this.stateDisturbance = new DenseMatrix64F(rd, rd);
        multOuter(R, stateDisturbance);
        this.predictedState = new DenseMatrix64F(rd, 1, true, new double[rd]);
        this.filteredState = new DenseMatrix64F(rd, 1, true, new double[rd]);
        this.predictedStateCovariance = initializePredictedCovariance(ss);
        this.filteredStateCovariance = new DenseMatrix64F(rd, rd);
        this.predictionErrorVariance = new double[y.length];
        this.predictionError = new double[y.length];
        this.Z = new DenseMatrix64F(1, rd, true, ss.stateEffectsVector());
        this.Zt = new DenseMatrix64F(rd, 1, true, new double[rd]);
        this.ZP = new DenseMatrix64F(1, rd, true, new double[rd]);
        this.PZtf = new DenseMatrix64F(rd, 1, true, new double[rd]);
        this.PZtfZ = new DenseMatrix64F(rd, rd, true, new double[rd * rd]);
        this.kalmanOutput = filter();
    }

    private KalmanOutput filter() {

        int n = 0;
        double f;
        predictionError[0] = y[0];
        // f[t] is always the first element of the first column of the predicted covariance matrix,
        // because f[t] = Z * M, where Z is a row vector with a 1 in the first (index 0) position and zeros elsewhere,
        // and M is the first column of the predicted covariance matrix.
        mult(Z, predictedStateCovariance, ZP);
        transpose(Z, Zt);
        predictionErrorVariance[0] = dot(ZP, Zt);
        f = predictionErrorVariance[0];

        double ssq = 0.0;
        double sumlog = 0.0;
        if (f < 1E4) {
            n++;
            ssq = ((predictionError[0] * predictionError[0]) / f);
            sumlog = log(f);

        }
        // Initialize filteredState.
        DenseMatrix64F newInfo = new DenseMatrix64F(rd, 1, true, new double[rd]);
        transpose(ZP, newInfo);
        divide(newInfo, f);
        PZtf = newInfo.copy();
        scale(predictionError[0], newInfo);
        add(predictedState, newInfo, filteredState);

        // Initialize filteredCovariance.
        final RowD1Matrix64F adjustedPredictionCovariance = new DenseMatrix64F(rd, rd);
        mult(PZtf, Z, PZtfZ);
        mult(PZtfZ, predictedStateCovariance, adjustedPredictionCovariance);
        //multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
        //divide(adjustedPredictionCovariance, predictionErrorVariance[0]);
        subtract(predictedStateCovariance, adjustedPredictionCovariance, filteredStateCovariance);

        final RowD1Matrix64F filteredCovarianceTransition = new DenseMatrix64F(rd, rd);
        final RowD1Matrix64F stateCovarianceTransition = new DenseMatrix64F(rd, rd);
        final DenseMatrix64F transitionTranspose = transitionMatrix.copy();
        transpose(transitionTranspose);

        predictionError[0] /= Math.sqrt(f);


        for (int t = 1; t < y.length; t++) {

            // Update predicted mean of the state vector.
            mult(transitionMatrix, filteredState, predictedState);

            // Update predicted covariance of the state vector.
            mult(transitionMatrix, filteredStateCovariance, filteredCovarianceTransition);
            mult(filteredCovarianceTransition, transitionTranspose, stateCovarianceTransition);
            add(stateCovarianceTransition, stateDisturbance, predictedStateCovariance);

            predictionError[t] = y[t] - dot(Z, predictedState);
            mult(Z, predictedStateCovariance, ZP);
            predictionErrorVariance[t] = dot(ZP, Zt);
            f = predictionErrorVariance[t];
            if (f < 1E4) {
                n++;
                ssq += ((predictionError[t] * predictionError[t]) / f);
                sumlog += log(f);
            }

            // Update filteredState.
            transpose(ZP, newInfo);
            //mult(predictedStateCovariance, Zt, newInfo);
            divide(newInfo, f);
            PZtf = newInfo.copy();
            scale(predictionError[t], newInfo);
            add(predictedState, newInfo, filteredState);

            // Update filteredCovariance.
            mult(PZtf, Z, PZtfZ);
            mult(PZtfZ, predictedStateCovariance, adjustedPredictionCovariance);
            //multOuter(predictedCovarianceFirstColumn, adjustedPredictionCovariance);
            //divide(adjustedPredictionCovariance, predictionErrorVariance[0]);
            subtract(predictedStateCovariance, adjustedPredictionCovariance, filteredStateCovariance);

            predictionError[t] /= Math.sqrt(f);
        }
        return new KalmanOutput(n, ssq, sumlog, predictionError);
    }

    private DenseMatrix64F initializePredictedCovariance(final StateSpaceARIMA ss) {
        DenseMatrix64F P0 = new DenseMatrix64F(rd, rd);
        double[] P = getInitialStateCovariance(ss.arParams(), ss.maParams());
        DenseMatrix64F arMatrix = new DenseMatrix64F(r, r, true, unpack(P));
        double[] kappa = new double[d * d];
        for (int i = 0; i < d; i++) {
            kappa[i * d + i] = 1E6;
        }
        DenseMatrix64F kappaMatrix = new DenseMatrix64F(d, d, true, kappa);
        insert(arMatrix, P0, 0, 0);
        insert(kappaMatrix, P0, r, r);
        return P0;
    }

    /**
     * Adapted from <a href="https://www.stat.berkeley.edu/classes/s244/as154.pdf">algorithm AS 154</a> with guidance
     * from <a href="https://github.com/SurajGupta/r-source/blob/master/src/library/stats/src/arima.c#L1009">arima.c</a>
     *
     * @param phi   the autoregressive parameters.
     * @param theta the moving-average parameters.
     * @return the initial covariance matrix of the state variables as a lower triangular matrix in a 1D array.
     */
    private static double[] getInitialStateCovariance(final double[] phi, final double[] theta) {
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

    private static void inclu2(final int np, final double[] xnext, final double[] xrow, final double ynext,
                               final double[] d, final double[] rbar, final double[] thetab) {

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

    private static void regres(final int np, final int nrbar, final double[] rbar, final double[] thetab,
                               final double[] beta) {
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

    private static double[] unpack(final double[] triangularMatrix) {
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

    KalmanOutput output() {
        return this.kalmanOutput;
    }

    double[] predictionError() {
        return this.predictionError.clone();
    }

    double ssq() {
        return this.kalmanOutput.ssq();
    }

    int n() {
        return this.kalmanOutput.n();
    }

    double sumLog() {
        return this.kalmanOutput.sumLog();
    }

    double logLikelihood() {
        return this.kalmanOutput.logLikelihood();
    }

    static class KalmanOutput {

        private final int n;
        private final double ssq;
        private final double sumlog;
        private final double sigma2;
        private final double logLikelihood;
        private final double[] residuals;

        KalmanOutput(final int n, final double ssq, final double sumlog, final double[] residuals) {
            this.n = n;
            this.ssq = ssq;
            this.sumlog = sumlog;
            this.sigma2 = ssq / n;
            this.logLikelihood = (-n / 2.0) * (log(2 * PI * sigma2) + 1.0) - (0.5 * sumlog);
            this.residuals = residuals.clone();
        }

        double ssq() {
            return this.ssq;
        }

        int n() {
            return this.n;
        }

        double sumLog() {
            return this.sumlog;
        }

        double sigma2() {
            return this.sigma2;
        }

        double logLikelihood() {
            return this.logLikelihood;
        }

        double[] residuals() {
            return this.residuals.clone();
        }

    }

}
