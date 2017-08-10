package timeseries.model.arima;

final class ArmaStateSpace {

    private final double[] differencedSeries;
    private final double[] arParams;
    private final double[] maParams;
    private final double[][] transitionMatrix;
    private final double[] disturbanceVector;
    private final double[] observationVector;
    private final int r; // r = max(p, q + 1).

    ArmaStateSpace(final double[] differencedSeries, final double[] arParams, final double[] maParams) {
        this.differencedSeries = differencedSeries.clone();
        this.arParams = arParams.clone();
        this.maParams = maParams.clone();
        r = Math.max(arParams.length, maParams.length + 1);
        this.transitionMatrix = createTransitionMatrix();
        this.disturbanceVector = createMovingAverageVector();
        this.observationVector = createStateEffectsVector();
    }

    private double[] createStateEffectsVector() {
        double[] Z = new double[r];
        Z[0] = 1.0;
        return Z;
    }


    private double[] createMovingAverageVector() {
        double[] R = new double[r];
        R[0] = 1.0;
        System.arraycopy(maParams, 0, R, 1, maParams.length);
        return R;
    }

    private double[][] createTransitionMatrix() {
        double[][] T = new double[r][r];
        for (int i = 0; i < arParams.length; i++) {
            T[i][0] = arParams[i];
        }
        for (int i = 1; i < r; i++) {
            T[i - 1][i] = 1;
        }
        return T;
    }

    double[] differencedSeries() {
        return differencedSeries.clone();
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
}
