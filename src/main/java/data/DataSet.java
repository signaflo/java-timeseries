/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package data;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;

import stats.Statistics;

/**
 * A collection of numerical observations. This class is immutable and all subclasses must be immutable.
 * @author Jacob Rachiele
 *
 */
public class DataSet {
	
	private final double[] data;
	
	/**
	 * Construct a new DataSet from the given data.
	 * @param data the collection of observations.
	 */
	public DataSet(final double... data) {
		if (data == null) {
			throw new IllegalArgumentException("Null array passed to constructor.");
		}
		this.data = data.clone();
	}
	
	/**
	 * The sum of the observations.
	 * @return the sum of the observations.
	 */
	public final double sum() {
		return Statistics.sumOf(this.data);
	}
	
	/**
	 * The sum of the squared observations.
	 * @return the sum of the squared observations.
	 */
	public final double sumOfSquares() {
	  return Statistics.sumOfSquared(this.data);
	}

	/**
	 * The mean of the observations.
	 * @return the mean of the observations.
	 */
	public final double mean() {
		return Statistics.meanOf(this.data);
	}

	/**
	 * The median value of the observations.
	 * @return the median value of the observations.
	 */
	public final double median() {
		return Statistics.medianOf(this.data);
	}
	
	/**
	 * The size of the DataSet.
	 * @return the size of the DataSet.
	 */
	public final int n() {
		return this.data.length;
	}

	/**
	 * Multiply every element of this DataSet with the corresponding element of the given DataSet.
	 * @param otherData The data to multiply by.
	 * @return A new DataSet containing every element of this DataSet multiplied by
	 * the corresponding element of the given DataSet.
	 */
	public final DataSet times(final DataSet otherData) {
		return new DataSet(Operators.productOf(this.data, otherData.data));
	}

	/**
	 * Add every element of this DataSet with the corresponding element of the given DataSet.
	 * @param otherData The data to add to.
	 * @return A new DataSet containing every element of this DataSet added to
	 * the corresponding element of the given DataSet.
	 */
	public final DataSet plus(final DataSet otherData) {
		return new DataSet(Operators.sumOf(this.data, otherData.data));
	}
	
	/**
	 * The unbiased sample variance of the observations.
	 * @return the unbiased sample variance of the observations.
	 */
	public final double variance() {
		return Statistics.varianceOf(this.data);
	}
	
	/**
	 * The unbiased sample standard deviation of the observations.
	 * @return the unbiased sample standard deviation of the observations.
	 */
	public final double stdDeviation() {
		return Statistics.stdDeviationOf(this.data);
	}
	
	/**
	 * The unbiased sample covariance of these observations with the observations
	 * contained in the given DataSet.
	 * @param otherData the data to compute the covariance with.
	 * @return the unbiased sample covariance of these observations with the observations
	 * contained in the given DataSet.
	 */
	public final double covariance(final DataSet otherData) {
		return Statistics.covarianceOf(this.data, otherData.data);
	}
	
	/**
	 * The unbiased sample correlation of these observations with the observations
	 * contained in the given DataSet.
	 * @param otherData the data to compute the correlation coefficient with.
	 * @return the unbiased sample correlation of these observations with the observations
	 * contained in the given DataSet.
	 */
	public final double correlation(DataSet otherData) {
		return Statistics.correlationOf(this.data, otherData.data);
	}
	
	/**
	 * The observations.
	 * @return the observations.
	 */
	public final double[] data() {
		return this.data.clone();
	}

  /**
   * Plot this data set. This method will produce a scatter plot of the data values against the integers
   * from 0 to n - 1, where n is the size of the data set.
   */
	public void plot() {
		final double[] indices = new double[this.data.length];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
		}
		XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).
				title("Scatter Plot").xAxisTitle("Index").yAxisTitle("Values").build();
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).
		    setChartFontColor(Color.BLACK).setSeriesColors(new Color[] {Color.BLUE});
		chart.addSeries("data values", indices, data);
	    new SwingWrapper<>(chart).displayChart();
	}

	/**
	 * Plot this data set against the given data set. The given data set will be plotted on the x-axis, while
   * this data set will be plotted on the y-axis.
	 * @param otherData the data set to plot this data set against.
	 */
	public void plotAgainst(final DataSet otherData) {
		XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
				.title("Scatter Plot").xAxisTitle("X").yAxisTitle("Y").build();
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter).
	    setChartFontColor(Color.DARK_GRAY).setSeriesColors(new Color[] {Color.BLUE});
		chart.addSeries(" ", otherData.data, this.data);
		new SwingWrapper<>(chart).displayChart();
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.##");
		return "\nValues: " + Arrays.toString(data) + "\nSize: " + data.length + "\nMean: " + mean() + "\nStandard deviation: " + df.format(stdDeviation());
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
		DataSet other = (DataSet) obj;
		return Arrays.equals(data, other.data);
	}

}
