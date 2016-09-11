package timeseries.models.arima;

public final class StateSpaceARMA {
  
  private final double[] differencedSeries;
  private final double[] arParams;
  private final double[] maParams;
  private final double[][] F;
  private final int m;
  
  StateSpaceARMA(final double[] differencedSeries, final double[] arParams, final double[] maParams) {
    this.differencedSeries = differencedSeries.clone();
    this.arParams = arParams.clone();
    this.maParams = maParams.clone();
    m = Math.max(arParams.length, maParams.length);
    this.F = new double[m][m];
    for (int i = 0; i < arParams.length; i++) {
      this.F[i][0] = arParams[i];
    }
    for (int i = 1; i < m; i++) {
      this.F[i - 1][i] = 1;
    }
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
    return m;
  }
  
  public final double[] g() {
    double[] g = new double[m];
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
    double[][] H = new double[m][m];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m; j++) {
        H[i][j] = g[i] * g[j];
      }
    }
    return H;
  }
  
  public final double[][] F() {
    return this.F.clone();
  }

}
