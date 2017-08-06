package data.regression;

import data.DoublePair;

/**
 * A prediction of an unobserved response.
 *
 * @author Jacob Rachiele
 * Aug. 05, 2017
 */
public interface Prediction {

    /**
     * Get the point estimate.
     *
     * @return the point estimate.
     */
    double estimate();


}
