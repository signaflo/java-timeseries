package timeseries;

import linear.doubles.Vector;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 24, 2017
 */
public class UnevenlySpacedTimeSeriesSpec {

    private final List<Double> observationTimes = Arrays.asList(0.5, 1.5, 3.0);
    private final List<Vector> observations = Arrays.asList(
            Vector.from(5.0, 3.0),
            Vector.from(2.5, 10.0),
            Vector.from(4.5, 1.0));
    private UnevenlySpacedTimeSeries timeSeries = new UnevenlySpacedTimeSeries(observationTimes, observations);

    @Test
    public void whenPreviousObservationTimeThenMostRecentObservationTimeReturned() {
        Double previousObservationTime = timeSeries.previousObservationTime(2.0);
        assertThat(previousObservationTime, is(1.5));
        previousObservationTime = timeSeries.previousObservationTime(4.0);
        assertThat(previousObservationTime, is(3.0));
        previousObservationTime = timeSeries.previousObservationTime(1.0);
        assertThat(previousObservationTime, is(0.5));
        previousObservationTime = timeSeries.previousObservationTime(0.0);
        assertThat(previousObservationTime, is(0.5));
        previousObservationTime = timeSeries.previousObservationTime(1.5);
        assertThat(previousObservationTime, is(1.5));
        previousObservationTime = timeSeries.previousObservationTime(0.5);
        assertThat(previousObservationTime, is(0.5));
        previousObservationTime = timeSeries.previousObservationTime(3.0);
        assertThat(previousObservationTime, is(3.0));
    }

    @Test
    public void whenNextObservationTimeThenNextObservationTimeReturned() {
        Double nextObservationTime = timeSeries.nextObservationTime(2.0);
        assertThat(nextObservationTime, is(3.0));
        nextObservationTime = timeSeries.nextObservationTime(4.0);
        assertThat(nextObservationTime, is(Double.POSITIVE_INFINITY));
        nextObservationTime = timeSeries.nextObservationTime(1.0);
        assertThat(nextObservationTime, is(1.5));
        nextObservationTime = timeSeries.nextObservationTime(0.0);
        assertThat(nextObservationTime, is(0.5));
        nextObservationTime = timeSeries.nextObservationTime(1.5);
        assertThat(nextObservationTime, is(1.5));
        nextObservationTime = timeSeries.nextObservationTime(0.5);
        assertThat(nextObservationTime, is(0.5));
        nextObservationTime = timeSeries.nextObservationTime(3.0);
        assertThat(nextObservationTime, is(3.0));
    }
}
