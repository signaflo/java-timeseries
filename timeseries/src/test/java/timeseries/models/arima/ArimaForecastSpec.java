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

package timeseries.models.arima;

import timeseries.TestData;
import org.junit.Test;
import timeseries.TimeSeries;
import timeseries.models.Forecast;

import static org.junit.Assert.*;

public class ArimaForecastSpec {

    @Test
    public void whenForecastThenCorrectPredictionIntervals() {
        TimeSeries timeSeries = TestData.debitcards;
        Arima.FittingStrategy fittingStrategy = Arima.FittingStrategy.CSSML;
        ArimaCoefficients coefficients = ArimaCoefficients.builder()
                                                          .setMACoeffs(-0.6760904)
                                                          .setSeasonalMACoeffs(-0.5718134)
                                                          .setDifferences(1)
                                                          .setSeasonalDifferences(1)
                                                          .setSeasonalFrequency(12)
                                                          .build();
        Arima model = Arima.model(timeSeries, coefficients, fittingStrategy);
        ArimaForecaster forecaster = new ArimaForecaster(model);
        Forecast forecast = forecaster.forecast(12);
        double[] expectedLower = {17812.16355, 17649.219039, 18907.15779, 18689.915865, 21405.818889, 21379.160025,
                22115.94079, 23456.237366, 19763.2863, 20061.21154, 19606.74272, 25360.633656};
        double[] expectedUpper = {21145.198098, 21152.740005, 22573.24549, 22511.661393, 25377.125821, 25494.596649,
                26370.627437, 27845.75879, 24283.622371, 24708.68161, 24377.960375, 30252.469485};
        double[] lower = forecast.lowerPredictionInterval().asArray();
        double[] upper = forecast.upperPredictionInterval().asArray();
        assertArrayEquals(expectedLower, lower, 1E-1);
        assertArrayEquals(expectedUpper, upper, 1E-1);
    }

    @Test
    public void whenArimaForecastThenForecastValuesCorrect() {
        TimeSeries timeSeries = TestData.debitcards;
        Arima.FittingStrategy fittingStrategy = ArimaModel.FittingStrategy.CSSML;
        ArimaCoefficients coefficients = ArimaCoefficients.builder()
                                                          .setMACoeffs(-0.6760904)
                                                          .setSeasonalMACoeffs(-0.5718134)
                                                          .setDifferences(1)
                                                          .setSeasonalDifferences(1)
                                                          .setSeasonalFrequency(12)
                                                          .build();
        Arima model = Arima.model(timeSeries, coefficients, fittingStrategy);
        ArimaForecaster forecaster = new ArimaForecaster(model);
        Forecast forecast = forecaster.forecast(24);
        double[] expectedForecast = {19478.680824, 19400.979522, 20740.20164, 20600.788629, 23391.472355, 23436.878337,
                24243.284113, 25650.998078, 22023.454336, 22384.946575, 21992.351548, 27806.551571, 20452.304145,
                20374.602843, 21713.824961, 21574.411949, 24365.095675, 24410.501658, 25216.907434, 26624.621399,
                22997.077656, 23358.569896, 22965.974868, 28780.174891};
        double[] fcst = forecast.pointForecast().asArray();
        assertArrayEquals(expectedForecast, fcst, 1E-1);
    }
}
