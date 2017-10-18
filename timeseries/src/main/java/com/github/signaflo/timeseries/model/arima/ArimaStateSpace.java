package com.github.signaflo.timeseries.model.arima;

import static com.github.signaflo.math.operations.DoubleFunctions.combine;

final class ArimaStateSpace {

    private final double[] observations;
    private final double[] arParams;
    private final double[] maParams;
    private final double[] delta;
    private final double[][] transitionMatrix;
    private final double[] disturbanceVector;
    private final double[] observationVector;
    private final int r; // r = max(p, q + 1).
    private final int d;

    ArimaStateSpace(double[] observations, double[] arParams, double[] maParams, double[] delta) {
        this.observations = observations.clone();
        this.arParams = arParams.clone();
        this.maParams = maParams.clone();
        this.delta = delta.clone();
        this.r = Math.max(arParams.length, maParams.length + 1);
        this.d = delta.length;
        this.transitionMatrix = createTransitionMatrix();
        this.disturbanceVector = createMovingAverageVector();
        this.observationVector = createStateEffectsVector();
    }

    private double[] createStateEffectsVector() {
        double[] Z = new double[r];
        Z[0] = 1.0;
        return combine(Z, delta);
    }


    private double[] createMovingAverageVector() {
        double[] R = new double[r + d];
        R[0] = 1.0;
        System.arraycopy(maParams, 0, R, 1, maParams.length);
        return R;
    }

    private double[][] createTransitionMatrix() {
        double[][] T = new double[r + d][r + d];
        for (int i = 0; i < arParams.length; i++) {
            T[i][0] = arParams[i];
        }
        for (int i = 1; i < r; i++) {
            T[i - 1][i] = 1;
        }
        for (int i = 0; i < d; i++) {
            T[r][r + i] = delta[i];
        }
        if (d > 0) {
            T[r][0] = 1.0;
        }
        for (int i = 0; i < d - 1; i++) {
            T[r + i + 1][r + i] = 1.0;
            T[r][r + i] = delta[i];
        }
        return T;
    }

    double[] observations() {
        return observations.clone();
    }

    double[] arParams() {
        return arParams.clone();
    }

    double[] maParams() {
        return maParams.clone();
    }

    final double[][] transitionMatrix() {
        return this.transitionMatrix.clone();
    }

    final double[] movingAverageVector() {
        return this.disturbanceVector.clone();
    }

    final double[] stateEffectsVector() {
        return this.observationVector.clone();
    }

    final int r() {
        return this.r;
    }

    final int d() {
        return this.d;
    }

}
