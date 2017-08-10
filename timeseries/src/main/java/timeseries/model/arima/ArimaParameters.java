/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package timeseries.model.arima;

import lombok.Data;
import lombok.NonNull;

/**
 * The parameters of an ARIMA model. The main difference between this class and {@link ArimaCoefficients} is that
 * the coefficients represent fixed, unchanging quantities that are either known or have been estimated,
 * whereas the parameters represent the coefficients before they are known, or before they have been fully estimated.
 * For this reason, the ArimaCoefficients class is immutable while the variables of this class may be updated after
 * they have been initialized.
 */
@Data
final class ArimaParameters {

    private static final double EPSILON = Math.ulp(1.0);

    private @NonNull double[] autoRegressivePars;
    private @NonNull double[] movingAveragePars;
    private @NonNull double[] seasonalAutoRegressivePars;
    private @NonNull double[] seasonalMovingAveragePars;
    private double mean = 0.0;
    private double intercept = 0.0;
    private double drift = 0.0;
    private double meanParScale = 1.0;
    private double interceptParScale = 1.0;
    private double driftParScale = 1.0;

//    ArimaParameters(int numAR, int numMA, int numSAR, int numSMA) {
//        this.autoRegressivePars = new double[numAR];
//        this.movingAveragePars = new double[numMA];
//        this.seasonalAutoRegressivePars = new double[numSAR];
//        this.seasonalMovingAveragePars = new double[numSMA];
//    }

//    double getScaledMean() {
//        return this.mean / this.meanParScale;
//    }
//
//    double getScaledIntercept() {
//        return this.intercept / this.interceptParScale;
//    }
//
//    double getScaledDrift() {
//        return this.drift / this.driftParScale;
//    }

    void setAndScaleMean(final double meanFactor) {
        this.mean = meanFactor * this.meanParScale;
    }

    void setAndScaleIntercept(final double interceptFactor) {
        this.intercept = interceptFactor * this.interceptParScale;
    }

    void setAndScaleDrift(final double driftFactor) {
        this.drift = driftFactor * this.driftParScale;
    }

    double[] getRegressors(final ArimaOrder order) {
        double[] regressors = new double[order.npar() - order.sumARMA()];
        if (order.constant().include()) {
            regressors[0] = this.mean;
        }
        if (order.drift().include()) {
            regressors[order.constant().asInt()] = this.drift;
        }
        return regressors;
    }

    double[] getAll(ArimaOrder order) {
        double[] pars = new double[order.npar()];
        System.arraycopy(autoRegressivePars, 0, pars, 0, autoRegressivePars.length);
        System.arraycopy(movingAveragePars, 0, pars, order.p(), movingAveragePars.length);
        System.arraycopy(seasonalAutoRegressivePars, 0, pars, order.p() + order.q(),
                         seasonalAutoRegressivePars.length);
        System.arraycopy(seasonalMovingAveragePars, 0, pars, order.p() + order.q() + order.P(),
                         seasonalMovingAveragePars.length);
        if (order.constant().include()) {
            pars[order.sumARMA()] = this.mean;
        }
        if (order.drift().include()) {
            pars[order.sumARMA() + order.constant().asInt()] = this.drift;
        }
        return pars;
    }

    double[] getAllScaled(ArimaOrder order) {
        double[] pars = new double[order.npar()];
        System.arraycopy(autoRegressivePars, 0, pars, 0, autoRegressivePars.length);
        System.arraycopy(movingAveragePars, 0, pars, order.p(), movingAveragePars.length);
        System.arraycopy(seasonalAutoRegressivePars, 0, pars, order.p() + order.q(),
                         seasonalAutoRegressivePars.length);
        System.arraycopy(seasonalMovingAveragePars, 0, pars, order.p() + order.q() + order.P(),
                         seasonalMovingAveragePars.length);
        if (order.constant().include()) {
            pars[order.sumARMA()] = this.mean / (this.meanParScale + EPSILON);
        }
        if (order.drift().include()) {
            pars[order.sumARMA() + order.constant().asInt()] = this.drift / (this.driftParScale + EPSILON);
        }
        return pars;
    }

    static ArimaParameters fromCoefficients(ArimaCoefficients coefficients) {
        ArimaParameters parameters = new ArimaParameters(coefficients.arCoeffs(),
                                                         coefficients.maCoeffs(),
                                                         coefficients.seasonalARCoeffs(),
                                                         coefficients.seasonalMACoeffs());
        parameters.setMean(coefficients.mean());
        parameters.setIntercept(coefficients.intercept());
        parameters.setDrift(coefficients.drift());
        return parameters;
    }

    static ArimaParameters fromOrder(ArimaOrder order) {
        return initializePars(order.p(), order.q(), order.P(), order.Q());
    }

    static ArimaParameters initializePars(int numAR, int numMA, int numSAR, int numSMA) {
        double[] autoRegressivePars = new double[numAR];
        double[] movingAveragePars = new double[numMA];
        double[] seasonalAutoRegressivePars = new double[numSAR];
        double[] seasonalMovingAveragePars = new double[numSMA];
        return new ArimaParameters(autoRegressivePars, movingAveragePars, seasonalAutoRegressivePars,
                                   seasonalMovingAveragePars);
    }

//    private ArimaParameters(ArimaParameters parameters) {
//        this.autoRegressivePars = parameters.autoRegressivePars.clone();
//        this.movingAveragePars = parameters.movingAveragePars.clone();
//        this.seasonalAutoRegressivePars = parameters.seasonalAutoRegressivePars.clone();
//        this.movingAveragePars = parameters.seasonalMovingAveragePars.clone();
//        this.mean = parameters.mean;
//        this.drift = parameters.drift;
//    }

}
