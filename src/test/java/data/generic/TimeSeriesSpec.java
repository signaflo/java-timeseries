package data.generic;

import math.Real;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TimeSeriesSpec {

    //private TimeSeries<Real> timeSeries = TestData.ausbeerSeries();

    @Test
    public void whenAutoCovarianceComputedTheResultIsCorrect() {
        TimeSeries<Real> series = DataSets.realSeriesFrom(10.0, 5.0, 4.5, 7.7, 3.4, 6.9);
        double[] acvf = new double[] { 4.889, -1.837, -0.407, 1.310, -1.917, 0.406 };
        for (int i = 0; i < acvf.length; i++) {
            assertThat(series.autoCovarianceAtLag(i).value(), is(closeTo(acvf[i], 1E-2)));
        }
    }
}
