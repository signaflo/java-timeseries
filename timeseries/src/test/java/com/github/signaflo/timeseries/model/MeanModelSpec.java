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

package com.github.signaflo.timeseries.model;

import com.github.signaflo.math.operations.DoubleFunctions;
import org.hamcrest.MatcherAssert;
import com.github.signaflo.timeseries.TestData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.TimeUnit;
import com.github.signaflo.timeseries.Ts;
import com.github.signaflo.timeseries.forecast.Forecast;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;

public class MeanModelSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private TimeSeries series = TestData.ausbeer;
    private MeanModel meanModel = new MeanModel(series);

    @Test
    public void whenConstructedWithNullSeriesThenNPE() {
        exception.expect(NullPointerException.class);
        new MeanModel(null);
    }

    @Test
    public void whenMeanModelThenCorrectResiduals() {
        double[] x = new double[] {1.0, 2.0, 3.0};
        series = Ts.newAnnualSeries(x);
        meanModel = new MeanModel(series);
        double[] expectedResiduals = new double[] {-1.0, 0.0, 1.0};
        assertArrayEquals(expectedResiduals, meanModel.predictionErrors().asArray(), 1E-15);
    }

    @Test
    public void whenMeanModelThenCorrectFit() {
        double[] x = new double[] {1.0, 2.0, 3.0};
        series = Ts.newAnnualSeries(x);
        meanModel = new MeanModel(series);
        double[] expectedFit = new double[] {2.0, 2.0, 2.0};
        assertArrayEquals(expectedFit, meanModel.fittedSeries().asArray(), 1E-15);
    }

    @Test
    public void whenMeanForecastComputedForecastValuesCorrect() {
        int h = 6;
        double[] expected = DoubleFunctions.fill(h, series.mean());
        TimeSeries pointForecast = meanModel.forecast(h).pointEstimates();
        assertArrayEquals(expected, pointForecast.asArray(), 1E-2);
    }

    @Test
    public void whenMeanForecastComputedFirstObservationTimeCorrect() {
        TimeSeries pointForecast = meanModel.forecast(6).pointEstimates();
        OffsetDateTime expectedTime = OffsetDateTime.of(2008, 10, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
        assertThat(pointForecast.observationTimes().get(0), is(equalTo(expectedTime)));
    }

    @Test
    public void whenMeanForecastComputedTimePeriodUnchanged() {
        TimeSeries pointForecast = meanModel.forecast(6).pointEstimates();
        TimeUnit timeUnit = TimeUnit.QUARTER;
        assertThat(pointForecast.samplingInterval().timeUnit(), is(equalTo(timeUnit)));
    }

    @Test
    public void whenMeanForecastThenPredictionIntervalsCorrect() {
        Forecast forecast = meanModel.forecast(6, 0.05);
        double[] lowerValues = {243.129289, 243.129289, 243.129289, 243.129289, 243.129289, 243.129289};
        double[] upperValues = {586.775924, 586.775924, 586.775924, 586.775924, 586.775924, 586.775924};
        assertArrayEquals(lowerValues, forecast.lowerPredictionInterval().asArray(), 1E-2);
        assertArrayEquals(upperValues, forecast.upperPredictionInterval().asArray(), 1E-2);
    }

    @Test
    public void testEqualsAndHashCode() {
        MeanModel model1 = new MeanModel(series);
        MeanModel model2 = new MeanModel(TestData.debitcards);
        MeanModel nullModel = null;
        String aNonModel = "";
        assertThat(meanModel, is(meanModel));
        assertThat(meanModel, is(model1));
        assertThat(meanModel.hashCode(), is(model1.hashCode()));
        MatcherAssert.assertThat(model1, is(not(model2)));
        MatcherAssert.assertThat(model2, is(not(nullModel)));
        MatcherAssert.assertThat(model2, is(not(aNonModel)));
    }

}
