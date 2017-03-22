package data;

import com.google.common.primitives.Doubles;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Circle;
import timeseries.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static data.DoubleFunctions.round;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 19, 2017
 */
public class Plots {

    private Plots() {
    }

    public static void plot(final TimeSeries timeSeries, final String title, final String seriesName) {
        new Thread(() -> {
            final List<Date> xAxis = new ArrayList<>(timeSeries.observationTimes().size());
            for (OffsetDateTime dateTime : timeSeries.observationTimes()) {
                xAxis.add(Date.from(dateTime.toInstant()));
            }
            List<Double> seriesList = Doubles.asList(round(timeSeries.asArray(), 2));
            final XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2).height(600).width(800)
                                                      .title(title).build();
            XYSeries residualSeries = chart.addSeries(seriesName, xAxis, seriesList);
            residualSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        }).start();
    }
}
