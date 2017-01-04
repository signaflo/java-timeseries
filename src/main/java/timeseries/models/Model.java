package timeseries.models;

import timeseries.TimeSeries;

/**
 * Represents a time series model, which attempts to capture the most important characteristics of the
 * underlying data generating process.
 *
 * @author Jacob Rachiele
 *
 */
public interface Model {

  /**
   * Produce a time series of point forecasts from this model up to the given number of steps ahead.
   * <p>
   * To obtain additional information about the forecast, such as prediction intervals, use the {@link #forecast}
   * method and the resulting {@link Forecast} object.
   * </p>
   *
   * @param steps the number of time periods ahead to forecast.
   * @return a time series of point forecasts from this model up to the given number of steps ahead.
   */
  TimeSeries pointForecast(int steps);

  /**
   * Produce a new forecast up to the given number of steps and with the given &alpha; significance level for
   * computing prediction intervals.
   * @param steps the number of time periods ahead to forecast.
   * @param alpha the probability that a future observation will fall outside the associated (1 - &alpha;)100%
   * prediction interval, given that the model is "correct". Note that the correctness of the model often comes with
   * a high degree of uncertainty and this should be taken into account when making decisions. In other words, the
   * provided prediction intervals will often be overly optimistic.
   * @return a new forecast up to the given number of steps ahead with the given significance level.
   */
  Forecast forecast(int steps, double alpha);

  /**
   * Get the series of observations.
   * @return the series of observations.
   */
  TimeSeries timeSeries();

  /**
   * Get the model fitted values, which are in-sample one-step ahead forecasts.
   * @return the model fitted values.
   */
  TimeSeries fittedSeries();

  /**
   * Get the model residuals, the difference between the observed values and the model fitted values.
   * @return the model residuals.
   */
  TimeSeries residuals();

  /**
   * Plot the model fit, which often displays the model fitted values and the observations in the same plot area.
   */
  void plotFit();

  /**
   * Plot the model residuals.
   */
  void plotResiduals();

}