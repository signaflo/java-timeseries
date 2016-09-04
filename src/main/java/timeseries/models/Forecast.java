package timeseries.models;

import timeseries.TimeSeries;

public interface Forecast {

  TimeSeries upperPredictionInterval(int steps, double alpha);

  TimeSeries lowerPredictionInterval(int steps, double alpha);

  void pastAndFuture();

  void plot();

}