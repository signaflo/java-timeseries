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

package timeseries.models.regression.primitive;

import org.junit.Test;
import timeseries.TestData;
import timeseries.TimeSeries;
import timeseries.models.regression.primitive.TimeSeriesLinearRegression.Seasonal;

import static org.junit.Assert.assertArrayEquals;

public class TimeSeriesLinearRegressionSpec {

    @Test
    public void whenTSLRFitThenBetaCorrect() {
        TimeSeries debitcards = TestData.debitcards;
        TimeSeriesLinearRegression.Builder tslmBuilder = TimeSeriesLinearRegression.builder()
                                                                                   .response(debitcards)
                                                                                   .seasonal(Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        double[] expected = {6568.304945, 92.552198, -344.706044, 590.510989, 367.035714, 2166.637363, 2343.239011,
                2621.840659, 3399.442308, 1197.505495, 1310.491758, 868.631868, 5646.618132};
        assertArrayEquals(expected, regression.beta(), 1E-6);
    }
}
