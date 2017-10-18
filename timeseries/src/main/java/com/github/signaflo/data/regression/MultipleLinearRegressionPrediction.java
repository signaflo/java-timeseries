package com.github.signaflo.data.regression;

import com.github.signaflo.data.DoublePair;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A prediction from a multiple linear regression model.
 *
 * @author Jacob Rachiele
 * Aug. 06, 2017
 */
@EqualsAndHashCode @ToString
class MultipleLinearRegressionPrediction implements LinearRegressionPrediction {

    private final double estimate;
    private final double fitStandardError;
    private final DoublePair confidenceInterval;
    private final DoublePair predictionInterval;

    MultipleLinearRegressionPrediction(final double estimate, final double fitStandardError,
                                       final DoublePair confidenceInterval, final DoublePair predictionInterval) {
        this.estimate = estimate;
        this.fitStandardError = fitStandardError;
        this.confidenceInterval = confidenceInterval;
        this.predictionInterval = predictionInterval;
    }


    @Override
    public double fitStandardError() {
        return this.fitStandardError;
    }

    @Override
    public DoublePair confidenceInterval() {
        return this.confidenceInterval;
    }

    @Override
    public DoublePair predictionInterval() {
        return this.predictionInterval;
    }

    @Override
    public double estimate() {
        return this.estimate;
    }
}
