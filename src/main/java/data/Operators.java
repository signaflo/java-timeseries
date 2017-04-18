/*
 * Copyright (c) 2016 Jacob Rachiele
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
package data;

/**
 * Static methods for performing vector operations on arrays.
 *
 * @author Jacob Rachiele
 */
public final class Operators {

    private Operators() {
    }

    /**
     * Take the element-by-element product of the two arrays and return the result in a new array.
     *
     * @param left  the first array to take the product with.
     * @param right the second array to take the product with.
     *
     * @return the element-by-element product of the two arrays.
     */
    public static double[] productOf(final double[] left, final double[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException("The data arrays must have the same length.");
        }
        final double[] product = new double[left.length];
        for (int i = 0; i < left.length; i++) {
            product[i] = left[i] * right[i];
        }
        return product;
    }

    /**
     * Take the element-by-element sum of the two arrays and return the result in a new array.
     *
     * @param left  the first array to take the sum with.
     * @param right the second array to take the sum with.
     * @return the element-by-element sum of the two arrays.
     */
    public static double[] sumOf(final double[] left, final double[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException("The data arrays must have the same length.");
        }
        final double[] sum = new double[left.length];
        for (int i = 0; i < left.length; i++) {
            sum[i] = left[i] + right[i];
        }
        return sum;
    }

    /**
     * Take the element-by-element difference of the two arrays and return the result in a new array.
     *
     * @param left  the first array to take the difference with.
     * @param right the second array to take the difference with.
     * @return the element-by-element difference of the two arrays.
     */
    public static double[] differenceOf(final double[] left, final double[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException("The data arrays must have the same length.");
        }
        final double[] difference = new double[left.length];
        for (int i = 0; i < left.length; i++) {
            difference[i] = left[i] - right[i];
        }
        return difference;
    }

//  static List<Double> differenceOf(final List<Double> left, final List<Double> right) {
//      if (left.size() != right.size()) {
//          throw new IllegalArgumentException("The lists must have the same length.");
//      }
//      final List<Double> difference = new ArrayList<>(left.size());
//      for (int i = 0; i < left.size(); i++) {
//          difference.add(left.get(i) - right.get(i));
//      }
//      return difference;
//  }

    /**
     * Take the element-by-element quotient of the two arrays and return the result in a new array.
     *
     * @param numerator   the array of numerators.
     * @param denominator the array of denominators.
     * @return the element-by-element quotient of the two arrays.
     */
    public static double[] quotientOf(final double[] numerator, final double[] denominator) {
        if (numerator.length != denominator.length) {
            throw new IllegalArgumentException("The data arrays must have the same length.");
        }
        final double[] quotient = new double[numerator.length];
        for (int i = 0; i < numerator.length; i++) {
            quotient[i] = numerator[i] / denominator[i];
        }
        return quotient;
    }

    /**
     * Scale the original data by alpha and return the result in a new array.
     *
     * @param original the data to be scaled.
     * @param alpha    the scaling factor.
     * @return the original data scaled by alpha.
     */
    public static double[] scale(final double[] original, final double alpha) {
        final double[] scaled = new double[original.length];
        for (int i = 0; i < original.length; i++) {
            scaled[i] = original[i] * alpha;
        }
        return scaled;
    }

    /**
     * Subtract the given value from each element of the supplied data and return the result in a new array.
     *
     * @param data  the data to subtract the value from.
     * @param value the value to be subtracted.
     * @return a new array with each element of the supplied data subtracted by the given value.
     */
    public static double[] subtract(final double[] data, final double value) {
        final double[] diff = new double[data.length];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = data[i] - value;
        }
        return diff;
    }

//  private static void subtractBang(final double[] data, final double value) {
//    for (int i = 0; i < data.length; i++) {
//      data[i] -= value;
//    }
//  }
}
