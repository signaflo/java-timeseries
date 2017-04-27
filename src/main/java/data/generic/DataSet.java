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
package data.generic;

import java.util.List;

interface DataSet<T> {

    /**
     * The sum of the observations.
     *
     * @return the sum of the observations.
     */
    T sum();

    /**
     * The sum of the squared observations.
     *
     * @return the sum of the squared observations.
     */
    T sumOfSquares();

    /**
     * The mean of the observations.
     *
     * @return the mean of the observations.
     */
    T mean();

    /**
     * The median value of the observations.
     *
     * @return the median value of the observations.
     */
    T median();

    /**
     * The size of the data set.
     *
     * @return the size of the data set.
     */
    int size();

    /**
     * Multiply every element of this data set with the corresponding element of the given data set.
     *
     * @param otherData The data to multiply by.
     * @return A new data set containing every element of this data set multiplied by
     * the corresponding element of the given data set.
     */
    DataSet<T> times(DataSet<T> otherData);

    /**
     * Add every element of this data set to the corresponding element of the given data set.
     *
     * @param otherData The data to add to.
     * @return A new data set containing every element of this data set added to
     * the corresponding element of the given data set.
     */
    DataSet<T> plus(DataSet<T> otherData);

    /**
     * The unbiased sample variance of the observations.
     *
     * @return the unbiased sample variance of the observations.
     */
    T variance();

    /**
     * The unbiased sample standard deviation of the observations.
     *
     * @return the unbiased sample standard deviation of the observations.
     */
    T stdDeviation();

    /**
     * The unbiased sample covariance of these observations with the observations
     * contained in the given data set.
     *
     * @param otherData the data to compute the covariance with.
     * @return the unbiased sample covariance of these observations with the observations
     * contained in the given data set.
     */
    T covariance(DataSet<T> otherData);

    /**
     * The unbiased sample correlation of these observations with the observations
     * contained in the given data set.
     *
     * @param otherData the data to compute the correlation coefficient with.
     * @return the unbiased sample correlation of these observations with the observations
     * contained in the given data set.
     */
    T correlation(DataSet<T> otherData);

    /**
     * The observations.
     *
     * @return the observations.
     */
    List<T> data();
}
