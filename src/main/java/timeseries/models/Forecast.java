package timeseries.models;

import timeseries.TimeSeries;

/**
 * Represents a fcst produced by a time series {@link Model}.
 * 
 * @author Jacob Rachiele
 *
 */
public interface Forecast {

  /**
   * The upper end points of the prediction interval.
   * 
   * @return the upper end points of the prediction interval.
   */
  TimeSeries upperPredictionValues();

  /**
   * The lower end points of the prediction interval.
   * 
   * @return the lower end points of the prediction interval.
   */
  TimeSeries lowerPredictionValues();

  /**
   * Compute the upper end points of a prediction interval with the given number of fcst steps and the provided
   * significance level &alpha;
   * 
   * @param steps the number of fcst steps.
   * @param alpha the significance level for the prediction intervals.
   * @return the upper end points of a prediction interval with the given number of fcst steps and the provided
   *         significance level &alpha;
   */
  TimeSeries computeUpperPredictionValues(int steps, double alpha);

  /**
   * Compute the lower end points of a prediction interval with the given number of fcst steps and the provided
   * significance level &alpha;
   * 
   * @param steps the number of fcst steps.
   * @param alpha the significance level for the prediction intervals.
   * @return the lower end points of a prediction interval with the given number of fcst steps and the provided
   *         significance level &alpha;
   */
  TimeSeries computeLowerPredictionValues(int steps, double alpha);

  /**
   * The point fcst.
   * 
   * @return the point fcst.
   */
  TimeSeries forecast();

  /**
   * Plot the forecasted values along with the historical data and prediction interval.
   */
  void plot();

  /**
   * Plot only the forecasted values and the corresponding prediction interval.
   */
  void plotForecast();

}