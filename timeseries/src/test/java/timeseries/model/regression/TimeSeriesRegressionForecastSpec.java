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

package timeseries.model.regression;

import org.junit.Test;
import timeseries.TestData;
import timeseries.TimeSeries;
import timeseries.forecast.Forecast;
import timeseries.model.regression.TimeSeriesLinearRegression.Seasonal;

import static org.junit.Assert.assertArrayEquals;

public class TimeSeriesRegressionForecastSpec {

    @Test
    public void whenForecastWithPeriodOffsetOfOneThenCorrectValues() {
        double[] expected = {19943.587, 20954.337, 20779.2537, 22624.1704, 22914.6704, 23251.4204, 24065.6704,
                22163.4204, 22243.337, 21952.4204, 26754.9204, 21210.6578, 21070.1645};
        TimeSeries debitcards = TestData.debitcards.timeSlice(1, 145);
        TimeSeriesLinearRegressionBuilder tslmBuilder =
                TimeSeriesLinearRegression.tsBuilder()
                                          .response(debitcards)
                                          .seasonal(Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        Forecast forecast = regression.forecast(13);
        assertArrayEquals(expected, forecast.pointEstimates().asArray(), 1E-4);
    }

    @Test
    public void whenForecastWithPeriodOffsetOfZeroThenCorrectValues() {
        double[] expected = {21099, 20846.8462, 21874.6154, 21743.6923, 23635.8462, 23905, 24276.1538, 25146.3077,
                23036.9231, 23242.4615, 22893.1538, 27763.6923, 22209.6264};
        TimeSeries debitcards = TestData.debitcards.timeSlice(1, 156);
        TimeSeriesLinearRegressionBuilder tslmBuilder =
                TimeSeriesLinearRegression.tsBuilder()
                                          .response(debitcards)
                                          .seasonal(Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        Forecast forecast = regression.forecast(13);
        assertArrayEquals(expected, forecast.pointEstimates().asArray(), 1E-4);
    }

    @Test
    public void whenForecastWithPeriodOffsetOfSixThenCorrectValues() {
        double[] expected = {23177.42, 23991.67, 22089.42, 22169.3367, 21878.42, 26680.92, 21130.9651, 20878.8113,
                21906.5805, 21775.6574, 23667.8113, 23936.9651, 24292.6128};
        TimeSeries debitcards = TestData.debitcards.timeSlice(1, 150);
        TimeSeriesLinearRegressionBuilder tslmBuilder =
                TimeSeriesLinearRegression.tsBuilder()
                                          .response(debitcards)
                                          .seasonal(Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        Forecast forecast = regression.forecast(13);
        assertArrayEquals(expected, forecast.pointEstimates().asArray(), 1E-4);
    }

    @Test
    public void whenForecastWithPeriodOffsetSixThenLowerPredictionBoundsCorrect() {
        double[] expected = new double[] {20764.743921, 21578.993921, 19676.743921, 19756.660588, 19465.743921,
                24268.243921, 18721.510341, 18469.356495, 19497.125725, 19366.202648, 21258.356495,
                21527.510341, 21872.111524};
        TimeSeries debitcards = TestData.debitcards.timeSlice(1, 150);
        TimeSeriesLinearRegressionBuilder tslmBuilder =
                TimeSeriesLinearRegression.tsBuilder()
                                          .response(debitcards)
                                          .seasonal(Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        Forecast forecast = regression.forecast(13);
        assertArrayEquals(expected, forecast.lowerPredictionInterval().asArray(), 1E-2);
    }
}
