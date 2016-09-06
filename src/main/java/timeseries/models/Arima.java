package timeseries.models;

import timeseries.TimeSeries;
import timeseries.TimeUnit;

/**
 * A potentially seasonal Autoregressive Integrated Moving Average model. This class is immutable and thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class Arima {

  private final TimeSeries observations;
  private TimeSeries fitted;
  private TimeSeries residuals;
  private final int cycleLength;
  // The number of parameters, degree of differencing, and constant flag.
  private final ModelOrder order;

  private double mean;
  // The intercept is equal to mean * (1 - (sum of AR coefficients))
  private double intercept;
  private ModelCoefficients coeffs;
  
  // Note: no need to copy since TimeSeries and ModelOrder are immutable;
  Arima(final TimeSeries observations, final ModelOrder order, final TimeUnit seasonalCycle) {
    this.observations = observations;
    this.order = order;
    this.cycleLength = (int)(observations.timeScale().frequencyPer(seasonalCycle) / observations.timeScaleLength());
    double[] initialParameters = setInitialParameters();
    System.out.println(initialParameters);
  }
  
  Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimeUnit seasonalCycle) {
    this.observations = observations;
    this.coeffs = coeffs;
    this.order = coeffs.extractModelOrder();
    this.cycleLength = (int)(observations.timeScale().frequencyPer(seasonalCycle) / observations.timeScaleLength());
  }

  private final double[] setInitialParameters() {
    // Set initial constant to the mean and all other parameters to zero.
    double[] initParams = new double[order.sumARMA() + order.constant];
    if (order.constant == 1) {
      initParams[initParams.length - 1] = observations.mean();
    }
    return initParams;
  }

  final double[] expandArCoefficients() {
    double[] arCoeffs = coeffs.arCoeffs;
    double[] sarCoeffs = coeffs.sarCoeffs;
    double[] arSarCoeffs = new double[arCoeffs.length + sarCoeffs.length * cycleLength];
    
    for (int i = 0; i < arCoeffs.length; i++) {
      arSarCoeffs[i] = arCoeffs[i];
    }
    for (int i = 0; i < sarCoeffs.length; i++) {
      arSarCoeffs[(i + 1) * cycleLength - 1] = sarCoeffs[i];
      for (int j = 0; j < arCoeffs.length; j++) {
        arSarCoeffs[(i + 1) * cycleLength + j] = -sarCoeffs[i] * arCoeffs[j];
      }
    }
    
    return arSarCoeffs;
  }
  
//  public static final TimeSeries simulate(final ModelCoefficients order, final int n) {
//    final double[] series = new double[n];
//
//  }

  /**
   * The order of an ARIMA model, consisting of the number of autoregressive and moving average parameters, along with
   * the degree of differencing and whether or not a constant is in the model. This class is immutable and thread-safe.
   * 
   * @author Jacob Rachiele
   *
   */
  public static final class ModelOrder {
    private final int p;
    private final int d;
    private final int q;
    private final int P;
    private final int D;
    private final int Q;
    private final int constant;

    public ModelOrder(final int p, final int d, final int q, final int P, final int D, final int Q,
        final boolean constant) {
      this.p = p;
      this.d = d;
      this.q = q;
      this.P = P;
      this.D = D;
      this.Q = Q;
      this.constant = (constant == true) ? 1 : 0;
    }

    // This returns the total number of nonseasonal and seasonal ARMA parameters.
    private final int sumARMA() {
      return this.p + this.q + this.P + this.Q;
    }
  }

  public static final class ModelCoefficients {

    private final double[] arCoeffs;
    private final double[] maCoeffs;
    private final double[] sarCoeffs;
    private final double[] smaCoeffs;
    private final int d;
    private final int D;
    private final double mean;
    private final double intercept;

    public ModelCoefficients(final double[] arCoeffs, final double[] maCoeffs, final double[] sarCoeffs,
        final double[] smaCoeffs, final int d, final int D, final double mean) {
      this.arCoeffs = arCoeffs.clone();
      this.maCoeffs = maCoeffs.clone();
      this.sarCoeffs = sarCoeffs.clone();
      this.smaCoeffs = smaCoeffs.clone();
      this.d = d;
      this.D = D;
      this.mean = mean;

      double arSum = 0.0;
      for (int i = 0; i < arCoeffs.length; i++) {
        arSum += arCoeffs[i];
      }
      for (int i = 0; i < sarCoeffs.length; i++) {
        arSum += sarCoeffs[i];
        for (int j = 0; j < arCoeffs.length; j++) {
          arSum -= sarCoeffs[i] * arCoeffs[j];
        }
      }
      this.intercept = mean * (1 - arSum);
    }

    public final ModelOrder extractModelOrder() {
      return new ModelOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
          (Math.abs(mean) > 1E-8));
    }

  }

}
