package timeseries.models;

import timeseries.TimeSeries;

/**
 * Represents a forecast produced by a time series {@link Model}.
 * @author Jacob Rachiele
 *
 */
public interface Forecast {
  
  /**
   * A time series of the upper prediction 
   * @return
   */
  TimeSeries upperPredictionValues();
  
  TimeSeries lowerPredictionValues();

  TimeSeries computeUpperPredictionValues(int steps, double alpha);

  TimeSeries computeLowerPredictionValues(int steps, double alpha);
  
  TimeSeries forecast();

  void pastAndFuture();

  void plot();

}