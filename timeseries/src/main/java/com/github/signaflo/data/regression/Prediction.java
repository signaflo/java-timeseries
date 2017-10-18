package com.github.signaflo.data.regression;

/**
 * A prediction of an unobserved response.
 *
 * @author Jacob Rachiele
 * Aug. 05, 2017
 */
public interface Prediction {

    /**
     * The point estimate for this prediction.
     *
     * @return the point estimate.
     */
    double estimate();


}
