package timeseries.models;

import timeseries.TimeSeries;

public interface Model {

  TimeSeries pointForecast(int steps);

  Forecast forecast(int steps, double alpha);

  TimeSeries timeSeries();

  TimeSeries fittedSeries();

  TimeSeries residuals();

  void plotFit();

  void plotResiduals();

}