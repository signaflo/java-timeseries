package timeseries;

import linear.doubles.Vector;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 22, 2017
 */
public class UnevenlySpacedTimeSeries {

    private final List<Double> observationTimes;
    private final List<Vector> observations;
    private final int n;

    UnevenlySpacedTimeSeries(List<Double> observationTimes, List<Vector> observations) {
        if (observationTimes.size() != observations.size()) {
            throw new IllegalArgumentException("The list of observation times and list of observations" +
                                               " must have the same length, but the list of observation times " +
                                               "had length " + observationTimes.size() +
                                               " and the list of observations had length " + observations.size());
        }
        if (observationTimes.size() == 0) {
            throw new IllegalArgumentException("The list of observation times cannot be empty.");
        }
        this.observationTimes = observationTimes;
        this.observations = observations;
        this.n = observations.size();
    }

    Double previousObservationTime(Double time) {
        return observationTimes.get(previousObservationTimeIndex(time));
    }

    int previousObservationTimeIndex(Double time) {
        if (time.compareTo(observationTimes.get(0)) >= 0) {
            int index = -Collections.binarySearch(observationTimes, time) - 1;
            if (index < 1) {
                return -index - 1;
            }
            return index - 1;
        } else {
            return 0;
        }
    }

    Double nextObservationTime(Double time) {
        int index = nextObservationTimeIndex(time);
        if (index >= observationTimes.size()) {
            return Double.POSITIVE_INFINITY;
        }
        return observationTimes.get(index);
    }

    int nextObservationTimeIndex(Double time) {
        if (time.compareTo(observationTimes.get(n - 1)) <= 0) {
            int index = -Collections.binarySearch(observationTimes, time) - 1;
            if (index < 0) {
                return -index - 1;
            }
            return index;
        } else {
            return observationTimes.size();
        }
    }
}
