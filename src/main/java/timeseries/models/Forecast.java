package timeseries.models;

import timeseries.TimeSeries;

/**
 * Represents a forecast produced by a time series {@link Model}.
 * 
 * @author Jacob Rachiele
 *
 */
public interface Forecast {

  /**
   * Get the upper end points of the prediction interval.
   * 
   * @return the upper end points of the prediction interval.
   */
  TimeSeries upperPredictionValues();

  /**
   * Get the lower end points of the prediction interval.
   * 
   * @return the lower end points of the prediction interval.
   */
  TimeSeries lowerPredictionValues();

  /**
   * Compute the upper end points of a prediction interval with the given number of forecast steps and the provided
   * &alpha; significance level.
   * 
   * @param steps the number of time periods ahead to forecast.
   * @param alpha the significance level for the prediction intervals.
   * @return the upper end points of a prediction interval with the given number of forecast steps and the provided
   *         &alpha; significance level.
   */
  TimeSeries computeUpperPredictionValues(int steps, double alpha);

  /**
   * Compute the lower end points of a prediction interval with the given number of forecast steps and the provided
   * significance level &alpha;
   * 
   * @param steps the number of time periods ahead to forecast.
   * @param alpha the significance level for the prediction intervals.
   * @return the lower end points of a prediction interval with the given number of forecast steps and the provided
   *         &alpha; significance level.
   */
  TimeSeries computeLowerPredictionValues(int steps, double alpha);

  /**
   * Get the point forecasts.
   * 
   * @return the point forecasts.
   */
  TimeSeries forecast();

  /**
   * Plot the forecast values along with the historical data and prediction interval.
   */
  void plot();

  /**
   * Plot only the forecast values and the corresponding prediction interval.
   */
  void plotForecast();

}