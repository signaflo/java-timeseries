/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package com.github.signaflo.data.visualization;

import com.github.signaflo.timeseries.Time;
import com.google.common.primitives.Doubles;
import com.github.signaflo.data.DataSet;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.SeriesMarkers;
import com.github.signaflo.timeseries.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.signaflo.math.operations.DoubleFunctions.round;

/**
 * Static methods for producing plots.
 *
 * @author Jacob Rachiele
 *         Mar. 19, 2017
 */
public class Plots {

    private Plots() {
    }

    /**
     * Plot a time series, connecting the observation times to the measurements.
     *
     * @param timeSeries the series to plot.
     */
    public static void plot(final TimeSeries timeSeries) {
        plot(timeSeries, "Time Series Plot");
    }

    /**
     * Plot a time series, connecting the observation times to the measurements.
     *
     * @param timeSeries the series to plot.
     * @param title      the title of the plot.
     */
    public static void plot(final TimeSeries timeSeries, final String title) {
        plot(timeSeries, title, "values");
    }

    /**
     * Plot a time series, connecting the observation times to the measurements.
     *
     * @param timeSeries the series to plot.
     * @param title      the title of the plot.
     * @param seriesName the name of the series to display.
     */
    public static void plot(final TimeSeries timeSeries, final String title, final String seriesName) {
        Thread plotThread = new Thread(() -> {
            final List<Date> xAxis = new ArrayList<>(timeSeries.observationTimes().size());
            for (Time time : timeSeries.observationTimes()) {
                xAxis.add(Date.from(time.toInstant()));
            }
            List<Double> seriesList = Doubles.asList(round(timeSeries.asArray(), 2));
            final XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2)
                                                      .height(600)
                                                      .width(800)
                                                      .title(title)
                                                      .build();
            XYSeries residualSeries = chart.addSeries(seriesName, xAxis, seriesList);
            residualSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

            JFrame frame = new JFrame(title);
            JPanel panel = new XChartPanel<>(chart);
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }

    /**
     * Plot the sample autocorrelations of the given time series up to the given lag.
     *
     * @param timeSeries the series to plot.
     * @param k the maximum lag to include in the acf plot.
     */
    public static void plotAcf(TimeSeries timeSeries, final int k) {
        final double[] lags = new double[k + 1];
        final double[] acf = timeSeries.autoCorrelationUpToLag(k);
        
        for (int i = 1; i < lags.length; i++) {
            lags[i] = i;
        }
        final double upper = (-1 / timeSeries.size()) + (2 / Math.sqrt(timeSeries.size()));
        final double lower = (-1 / timeSeries.size()) - (2 / Math.sqrt(timeSeries.size()));
        final double[] upperLine = new double[lags.length];
        final double[] lowerLine = new double[lags.length];
        for (int i = 0; i < lags.length; i++) {
            upperLine[i] = upper;
        }
        for (int i = 0; i < lags.length; i++) {
            lowerLine[i] = lower;
        }

        Thread plotThread = new Thread(() -> {
            XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2)
                                                .height(800)
                                                .width(1200)
                                                .title("Autocorrelations By Lag")
                                                .build();
            XYSeries series = chart.addSeries("Autocorrelation", lags, acf);
            XYSeries series2 = chart.addSeries("Upper Bound", lags, upperLine);
            XYSeries series3 = chart.addSeries("Lower Bound", lags, lowerLine);
            chart.getStyler()
                 .setChartFontColor(Color.BLACK)
                 .setSeriesColors(new Color[]{Color.BLACK, Color.BLUE, Color.BLUE});

            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            series2.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line)
                   .setMarker(SeriesMarkers.NONE)
                   .setLineStyle(SeriesLines.DASH_DASH);
            series3.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line)
                   .setMarker(SeriesMarkers.NONE)
                   .setLineStyle(SeriesLines.DASH_DASH);
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Autocorrelation by Lag");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }

    /**
     * Plot a data set. This method will produce a scatter plot of the data values against the integers
     * from 0 to n - 1, where n is the size of the data set.
     *
     * @param dataSet the data set to plot.
     */
    public static void plot(final DataSet dataSet) {
        Thread plotThread = new Thread(() -> {
            final double[] indices = new double[dataSet.size()];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }
            XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2).
                    title("Scatter Plot").xAxisTitle("Index").yAxisTitle("Values").build();
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).
                    setChartFontColor(Color.BLACK).setSeriesColors(new Color[]{Color.BLUE});
            chart.addSeries("com/github/signaflo/data", indices, dataSet.asArray());
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Data Set");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }

    /**
     * Plot the first data set against the second data set. The first data set will be plotted on the x-axis, while
     * the second data set will be plotted on the y-axis.
     *
     * @param firstDataSet the data set to plot on the x-axis.
     * @param secondDataSet the data set to plot against the first data set.
     */
    public static void plot(final DataSet firstDataSet, final DataSet secondDataSet) {
        Thread plotThread = new Thread(() -> {
            XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2)
                                                .height(600)
                                                .width(800)
                                                .title("Scatter Plot")
                                                .xAxisTitle("X")
                                                .yAxisTitle("Y")
                                                .build();
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).
                    setChartFontColor(Color.DARK_GRAY).setSeriesColors(new Color[]{Color.BLUE});
            chart.addSeries("Y against X", firstDataSet.asArray(), secondDataSet.asArray());
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Scatter Plot");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }


}
