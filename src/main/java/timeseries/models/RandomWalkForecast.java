package timeseries.models;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.None;

import com.google.common.primitives.Doubles;

import stats.distributions.Normal;
import timeseries.TimeSeries;

public final class RandomWalkForecast {
  
  private final RandomWalk model;
  private final TimeSeries forecast;
  private final TimeSeries upperInterval;
  private final TimeSeries lowerInterval;
  private final int steps;
  private final double alpha;
  
  public RandomWalkForecast(final RandomWalk model, final int steps, final double alpha) {
    this.model = model;
    this.forecast = model.forecast(steps);
    this.alpha = alpha;
    this.steps = steps;
    this.upperInterval = upperPredictionInterval(steps, alpha);
    this.lowerInterval = lowerPredictionInterval(steps, alpha);
  }
  
  public final TimeSeries upperPredictionInterval(final int steps, final double alpha) {
    double[] upperPredictionValues = new double[steps];
    double criticalValue = new Normal(0, model.residuals().stdDeviation()).quantile(1 - alpha/2);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) + criticalValue * Math.sqrt(t + 1);
    }
    return new TimeSeries(forecast.timeScale(), forecast.observationTimes().get(0), 
        forecast.periodLength(), upperPredictionValues);
  }
  
  public final TimeSeries lowerPredictionInterval(final int steps, final double alpha) {
    double[] upperPredictionValues = new double[steps];
    double criticalValue = new Normal(0, model.residuals().stdDeviation()).quantile(1 - alpha/2);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) - criticalValue * Math.sqrt(t + 1);
    }
    return new TimeSeries(forecast.timeScale(), forecast.observationTimes().get(0), 
        forecast.periodLength(), upperPredictionValues);
  }
  
  public final void plot() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
      final List<Date> xAxisObs = new ArrayList<>(model.timeSeries().n());
      for (OffsetDateTime dateTime : model.timeSeries().observationTimes()) {
        xAxisObs.add(Date.from(dateTime.toInstant()));
      }
      for (OffsetDateTime dateTime : forecast.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      
      double critValue = new Normal(0, model.residuals().stdDeviation()).quantile(1 - alpha/2);
      double[] errors = new double[forecast.n()];
      for (int t = 0; t < errors.length; t++) {
        errors[t] = critValue * Math.sqrt(t + 1);
      }
      
      List<Double> errorList = Doubles.asList(errors);
      List<Double> seriesList = Doubles.asList(model.timeSeries().series());
      List<Double> forecastList = Doubles.asList(forecast.series());
      List<Double> upperList = Doubles.asList(upperInterval.series());
      List<Double> lowerList = Doubles.asList(lowerInterval.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("Random Walk Forecast").build();
      
      XYSeries observationSeries = chart.addSeries("Observations", xAxisObs, seriesList);
      XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
//      XYSeries lowerSeries = chart.addSeries("Lower Interval", xAxis, lowerList);
//      XYSeries upperSeries = chart.addSeries("Upper Interval", xAxis, upperList);
//        
      observationSeries.setMarker(new None());
      forecastSeries.setMarker(new None());
//      lowerSeries.setMarker(new None());
//      upperSeries.setMarker(new None());
      
      observationSeries.setLineWidth(0.75f);
      forecastSeries.setLineWidth(0.75f);
//      lowerSeries.setLineWidth(0.75f);
//      upperSeries.setLineWidth(0.75f);
      
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED);
      observationSeries.setLineColor(Color.BLACK);
      forecastSeries.setLineColor(Color.BLUE);
//      lowerSeries.setLineColor(Color.RED);
//      upperSeries.setLineColor(Color.RED);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Random Walk Forecast");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

}
