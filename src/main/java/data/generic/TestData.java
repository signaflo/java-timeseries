package data.generic;

import math.Real;

import java.util.ArrayList;
import java.util.List;

class TestData {

    static TimeSeries<Real> ausbeerSeries() {
        double[] ausbeerArray = data.TestData.ausbeerArray();
        List<Real> ausbeerList = new ArrayList<>(ausbeerArray.length);
        for (double d : ausbeerArray) {
            ausbeerList.add(Real.from(d));
        }
        return DataSets.realSeriesFrom(ausbeerList);
    }
}
