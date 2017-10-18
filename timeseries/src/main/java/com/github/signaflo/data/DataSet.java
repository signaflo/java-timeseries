package com.github.signaflo.data;

/**
 * A collection of numerical observations.
 *
 * @author Jacob Rachiele
 *         Mar. 29, 2017
 */
public interface DataSet {
    /**
     * The sum of the observations.
     *
     * @return the sum of the observations.
     */
    double sum();

    /**
     * The sum of the squared observations.
     *
     * @return the sum of the squared observations.
     */
    double sumOfSquares();

    /**
     * The mean of the observations.
     *
     * @return the mean of the observations.
     */
    double mean();

    /**
     * The median value of the observations.
     *
     * @return the median value of the observations.
     */
    double median();

    /**
     * The size of the com.github.signaflo.data set.
     *
     * @return the size of the com.github.signaflo.data set.
     */
    int size();

    /**
     * Multiply every element of this com.github.signaflo.data set with the corresponding element of the given com.github.signaflo.data set.
     *
     * @param otherData The com.github.signaflo.data to multiply by.
     * @return A new com.github.signaflo.data set containing every element of this com.github.signaflo.data set multiplied by
     * the corresponding element of the given com.github.signaflo.data set.
     */
    DataSet times(DataSet otherData);

    /**
     * Add every element of this com.github.signaflo.data set to the corresponding element of the given com.github.signaflo.data set.
     *
     * @param otherData The com.github.signaflo.data to add to.
     * @return A new com.github.signaflo.data set containing every element of this com.github.signaflo.data set added to
     * the corresponding element of the given com.github.signaflo.data set.
     */
    DataSet plus(DataSet otherData);

    /**
     * The unbiased sample variance of the observations.
     *
     * @return the unbiased sample variance of the observations.
     */
    double variance();

    /**
     * The unbiased sample standard deviation of the observations.
     *
     * @return the unbiased sample standard deviation of the observations.
     */
    double stdDeviation();

    /**
     * The unbiased sample covariance of these observations with the observations
     * contained in the given com.github.signaflo.data set.
     *
     * @param otherData the com.github.signaflo.data to compute the covariance with.
     * @return the unbiased sample covariance of these observations with the observations
     * contained in the given com.github.signaflo.data set.
     */
    double covariance(DataSet otherData);

    /**
     * The unbiased sample correlation of these observations with the observations
     * contained in the given com.github.signaflo.data set.
     *
     * @param otherData the com.github.signaflo.data to compute the correlation coefficient with.
     * @return the unbiased sample correlation of these observations with the observations
     * contained in the given com.github.signaflo.data set.
     */
    double correlation(DataSet otherData);

    /**
     * The observations.
     *
     * @return the observations.
     */
    double[] asArray();
}
