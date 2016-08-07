package data;

import java.util.Arrays;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * A collection of numerical observations.
 * @author jacob
 *
 */
public class DataSet {
	
	private final double[] data;
	
	/**
	 * Construct a new DataSet from the given data.
	 * @param data the colletion of observations.
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
	 * The mean, or statistical average, of the observations.
	 * @return the mean, or statistical average, of the observations.
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
	 * The unbiased sample correlation coefficient of these observations with the observations
	 * contained in the given DataSet.
	 * @param otherData the data to compute the correlation cofficient with.
	 * @return the unbiased sample correlation coefficient of these observations with the observations
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
		return this.data;
	}
	
	public void plot() {
		final double[] indices = new double[this.data.length];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
		}
		XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800).
				title("Data plot").xAxisTitle("index").yAxisTitle("values").build();
		XYStyler styler = chart.getStyler();
		styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
		chart.addSeries("data", indices, data);
	    new SwingWrapper<>(chart).displayChart();
	}
	public final void plotAgainst(final DataSet otherData) {
		XYChart chart = QuickChart.getChart("DataSet", "x", "y", "data", this.data, otherData.data);
		new SwingWrapper<>(chart).displayChart();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("data: ").append(Arrays.toString(data)).
		append("\nsize: ").append(data.length).
		append("\nmean: ").append(mean()).
		append("\nstandard deviation: ").append(stdDeviation());
		return builder.toString();
	}

}
