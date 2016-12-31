package data;

import stats.Statistics;

import java.util.List;

/**
 * Static methods for creating, manipulating, and operating on arrays of primitive doubles.
 * @author Jacob Rachiele
 *
 */
public final class DoubleFunctions {

  private DoubleFunctions() {}
  
  /**
   * Create and return a new array from the given data.
   * @param data the data to create a new array from.
   * @return a new array from the given data.
   */
  public static double[] newArray(double... data) {
    return data.clone();
  }

  public static double[] combine(double[]... arrays) {
    int newArrayLength = 0;
    for (double[] array : arrays) {
      newArrayLength += array.length;
    }
    double[] newArray = new double[newArrayLength];
    newArrayLength = 0;
    for (double[] array : arrays) {
      System.arraycopy(array, 0, newArray, newArrayLength, array.length);
      newArrayLength += array.length;
    }
    return newArray;
  }

  public static double[] append(double[] original, double value) {
    double[] newArray = new double[original.length + 1];
    System.arraycopy(original, 0, newArray, 0, original.length);
    newArray[original.length] = value;
    return newArray;
  }

  public static double[] newArray(List<Double> data) {
    final int size = data.size();
    final double[] doubles = new double[size];
    for (int i = 0; i < size; i++) {
      doubles[i] = data.get(i);
    }
    return doubles;
  }

  public static double[] newArray(Double[] data) {
    final int size = data.length;
    final double[] doubles = new double[size];
    for (int i = 0; i < size; i++) {
      doubles[i] = data[i];
    }
    return doubles;
  }

  /**
   * Create and return a new array of the given size with every value set to the given value.
   * @param size the number of elements of the new array.
   * @param value the value to fill every element of the array with.
   * @return new array of the given size with every value set to the given value.
   */
  public static double[] fill(final int size, final double value) {
    final double[] filled = new double[size];
    for (int i = 0; i < filled.length; i++) {
      filled[i] = value;
    }
    return filled;
  }

  /**
   * Return a slice of the data between the given indices.
   * @param data the data to slice.
   * @param from the starting index.
   * @param to the ending index. The data at this index is excluded from the result.
   * @return a slice of the data between the given indices.
   */
  public static double[] slice(final double[] data, final int from, final int to) {
    final double[] sliced = new double[to - from];
    System.arraycopy(data, from, sliced, 0, to - from);
    return sliced;
  }

  /**
   * Transform the given data using a Box-Cox transformation with the given lambda value.
   * @param data the data to transform.
   * @param lambda the Box-Cox parameter.
   * @return the data transformed using a Box-Cox transformation with the given lambda value.
   */
  public static double[] boxCox(final double[] data, final double lambda) {
    final double[] boxCoxed = new double[data.length];
    if (Math.abs(lambda) < 1E-15) { 
      for (int i = 0; i < boxCoxed.length; i++) {
        boxCoxed[i] = Math.log(data[i]);
      }
      
    } else {
      for (int i = 0; i < boxCoxed.length; i++) {
        boxCoxed[i] = (Math.pow(data[i], lambda) - 1) / lambda;
      }
    }
    return boxCoxed;
  }

  /**
   * Invert the Box-Cox transformation, returning the original untransformed data.
   * @param data the transformed data to invert.
   * @param lambda the Box-Cox parameter used in the transformation.
   * @return the original, untransformed data in a new array.
   */
  public static double[] inverseBoxCox(final double[] data, final double lambda) {
    final double[] invBoxCoxed = new double[data.length];
    if (Math.abs(lambda) < 1E-15) {
      for (int i = 0; i < invBoxCoxed.length; i++) {
        invBoxCoxed[i] = Math.exp(data[i]);
      }
    } else {
      for (int i = 0; i < invBoxCoxed.length; i++) {
        invBoxCoxed[i] = Math.pow(data[i] * lambda + 1, 1 / lambda);
      }
    }
    return invBoxCoxed;
  }

  /**
   * Take the square root of each element of the given array and return the result in a new array.
   * @param data the data to take the square root of.
   * @return a new array containing the square root of each element.
   */
  public static double[] sqrt(final double[] data) {
    final double[] sqrtData = new double[data.length];
    for (int i = 0; i < sqrtData.length; i++) {
      sqrtData[i] = Math.sqrt(data[i]);
    }
    return sqrtData;
  }
  
  /**
   * Remove the mean from the given data and return the result in a new array.
   * @param data the data to remove the mean from.
   * @return the data with the mean removed.
   */
  public static double[] demean(final double[] data) {
    final double mean = Statistics.meanOf(data);
    final double[] demeaned = new double[data.length];
    for (int t = 0; t < data.length; t++) {
      demeaned[t] = data[t] - mean;
    }
    return demeaned;
  }

  /**
   * Take the additive inverse, or negative, of each element of the given array and return the result in a new array.
   * @param data the data to take the additive inverse of.
   * @return a new array containing the additive inverse, or negative, of each element.
   */
  public static double[] negativeOf(final double[] data) {
    final double[] negative = new double[data.length];
    for (int i = 0; i < negative.length; i++) {
      negative[i] = -data[i];
    }
    return negative;
  }
}
