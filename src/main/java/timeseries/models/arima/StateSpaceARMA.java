package timeseries.models.arima;

public final class StateSpaceARMA {
  
  private final double[] differencedSeries;
  private final double[] arParams;
  private final double[] maParams;
  private final double[][] T;
  private final double[] R;
  private final double[] Z;
  private final int r;
  
  public StateSpaceARMA(final double[] differencedSeries, final double[] arParams, final double[] maParams) {
    this.differencedSeries = differencedSeries.clone();
    this.arParams = arParams.clone();
    this.maParams = maParams.clone();
    r = Math.max(arParams.length, maParams.length + 1);
    this.T = createTransitionMatrix();
    this.R = createMovingAverageVector();
    this.Z = createStateEffectsVector();
  }
  
  private final double[] createStateEffectsVector() {
    double[] Z = new double[r];
    Z[0] = 1.0;
    return Z;
  }
  
  
  private final double[] createMovingAverageVector() {
    double[] R = new double[r];
    R[0] = 1.0;
    for (int i = 0; i < maParams.length; i++) {
      R[i + 1] = maParams[i];
    }
    return R;
  }
  private final double[][] createTransitionMatrix() {
    double[][] T = new double[r][r];
    for (int i = 0; i < arParams.length; i++) {
      T[i][0] = arParams[i];
    }
    for (int i = 1; i < r; i++) {
      T[i - 1][i] = 1;
    }
    return T;
  }

  public double[] differencedSeries() {
    return differencedSeries.clone();
  }

  public double[] arParams() {
    return arParams.clone();
  }

  public double[] maParams() {
    return maParams.clone();
  }

  public int m() {
    return r;
  }
  
  public final double[] g() {
    double[] g = new double[r];
    for (int i = 0; i < arParams.length; i++) {
      g[i] = arParams[i];
    }
    for (int j = 0; j < maParams.length; j++) {
      g[j] += maParams[j];
    }
    return g;
  }
  
  public final double[][] V() {
    double[] g = g();
    double[][] H = new double[r][r];
    for (int i = 0; i < r; i++) {
      for (int j = 0; j < r; j++) {
        H[i][j] = g[i] * g[j];
      }
    }
    return H;
  }
  
  public final double[][] transitionMatrix() {
    return this.T.clone();
  }
  
  public final double[] movingAverageVector() {
    return this.R.clone();
  }
  
  public final double[] stateEffectsVector() {
    return this.Z.clone();
  }

  public final int r() {
    return this.r;
  }
}
