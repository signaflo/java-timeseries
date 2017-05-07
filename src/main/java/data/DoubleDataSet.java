/*
 * Copyright (c) 2016 Jacob Rachiele
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
package data;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import stats.Statistics;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * A collection of numerical observations represented as primitive doubles. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class DoubleDataSet implements DataSet {

    private final double[] data;

    /**
     * Construct a new data set from the given data.
     *
     * @param data the collection of observations.
     */
    public DoubleDataSet(final double... data) {
        if (data == null) {
            throw new NullPointerException("Null array passed to DoubleDataSet constructor.");
        }
        this.data = data.clone();
    }

    @Override
    public final double sum() {
        return Statistics.sumOf(this.data);
    }

    @Override
    public final double sumOfSquares() {
        return Statistics.sumOfSquared(this.data);
    }

    @Override
    public final double mean() {
        return Statistics.meanOf(this.data);
    }

    @Override
    public final double median() {
        return Statistics.medianOf(this.data);
    }

    @Override
    public final int n() {
        return this.data.length;
    }

    @Override
    public final DataSet times(final DataSet otherData) {
        return new DoubleDataSet(Operators.productOf(this.data, otherData.asArray()));
    }

    @Override
    public final DataSet plus(final DataSet otherData) {
        return new DoubleDataSet(Operators.sumOf(this.data, otherData.asArray()));
    }

    @Override
    public final double variance() {
        return Statistics.varianceOf(this.data);
    }

    @Override
    public final double stdDeviation() {
        return Statistics.stdDeviationOf(this.data);
    }

    @Override
    public final double covariance(final DataSet otherData) {
        return Statistics.covarianceOf(this.data, otherData.asArray());
    }

    @Override
    public final double correlation(DataSet otherData) {
        return Statistics.correlationOf(this.data, otherData.asArray());
    }

    @Override
    public final double[] asArray() {
        return this.data.clone();
    }

    @Override
    public void plot() {
        new Thread(() -> {
            final double[] indices = new double[this.data.length];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }
            XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).
                    title("Scatter Plot").xAxisTitle("Index").yAxisTitle("Values").build();
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).
                    setChartFontColor(Color.BLACK).setSeriesColors(new Color[]{Color.BLUE});
            chart.addSeries("data", indices, data);
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Data Set");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        }).run();
    }

    @Override
    public void plotAgainst(final DataSet otherData) {
        new Thread(() -> {
            XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2)
                                                .height(600)
                                                .width(800)
                                                .title("Scatter Plot")
                                                .xAxisTitle("X")
                                                .yAxisTitle("Y")
                                                .build();
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).
                    setChartFontColor(Color.DARK_GRAY).setSeriesColors(new Color[]{Color.BLUE});
            chart.addSeries("Y against X", otherData.asArray(), this.data);
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Scatter Plot");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        }).run();
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.##");
        return "\nValues: " + Arrays.toString(data) + "\nLength: " + data.length + "\nMean: " + mean() +
               "\nStandard deviation: " + df.format(stdDeviation());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DoubleDataSet other = (DoubleDataSet) obj;
        return Arrays.equals(data, other.data);
    }

}
