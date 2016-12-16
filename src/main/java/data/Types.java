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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A class of static methods dealing with conversions between different data types.
 *
 * @author Jacob Rachiele
 * Date: Dec 09 2016
 *
 */
public class Types {

  private Types() {}

  /**
   * Test if the given string is representable as a double.
   * @param pi the string to test.
   * @return true if the string can be represented as a double and false otherwise.
   */
  public static boolean isDouble(String pi) {
    final String Digits     = "(\\p{Digit}+)";
    final String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    final String Exp        = "[eE][+-]?"+Digits;
    final String fpRegex    =
        "[\\x00-\\x20]*"+  // Optional leading "whitespace"
            "[+-]?(" + // Optional sign character
            "NaN|" +           // "NaN" string
            "Infinity|" +      // "Infinity" string

            // A decimal floating-point string representing a finite positive
            // number without a leading sign has at most five basic pieces:
            // Digits . Digits ExponentPart FloatTypeSuffix
            //
            // Since this method allows integer-only strings as input
            // in addition to strings of floating-point literals, the
            // two sub-patterns below are simplifications of the grammar
            // productions from section 3.10.2 of
            // The Java Language Specification.

            // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
            "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

            // . Digits ExponentPart_opt FloatTypeSuffix_opt
            "(\\.("+Digits+")("+Exp+")?)|"+

            // Hexadecimal strings
            "((" +
            // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HexDigits + "(\\.)?)|" +

            // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

            ")[pP][+-]?" + Digits + "))" +
            "[fFdD]?))" +
            "[\\x00-\\x20]*";// Optional trailing "whitespace"

    return Pattern.matches(fpRegex, pi);
  }

  /**
   * Convert the given list of strings to a list of doubles. This method returns the result in a new List.
   * @param strings the list of strings to convert.
   * @return the given list of strings converted to a list of doubles.
   */
  public static List<Double> toDoubleList(List<String> strings) {
    List<Double> doubles = new ArrayList<>(strings.size());
    for (String s : strings) {
      doubles.add(Double.valueOf(s));
    }
    return doubles;
  }

  /**
   * Convert the given list of strings to an array of doubles. This method returns the result in a new array.
   * @param strings the list of strings to convert.
   * @return the given list of strings converted to an array of doubles.
   */
  public static Double[] toDoubleArray(List<String> strings) {
    Double[] doubles = new Double[strings.size()];
    for (int i = 0; i < doubles.length; i++) {
      doubles[i] = Double.valueOf(strings.get(i));
    }
    return doubles;
  }

  /**
   * Convert the given list of strings to an array of strings. This method returns the result in a new array.
   * @param strings the list of strings to convert to an array.
   * @return the given list of strings converted to an array of strings.
   */
  public static String[] toStringArray(List<String> strings) {
    String[] stringArray = new String[strings.size()];
    for (int i = 0; i < stringArray.length; i++) {
      stringArray[i] = strings.get(i);
    }
    return stringArray;
  }

  public static boolean isLocalDateTime(final String s) {
    try {
      LocalDateTime.parse(s);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }

  // Shouldn't use exceptions for control flow, but parsing date-times is absurdly difficult to get right,
  // so using the fact that the OffsetDateTime parse method has already done it. Downside is that this
  // way of doing it will cause a performance hit.
  public static boolean isOffsetDateTime(final String s) {
    try {
      OffsetDateTime.parse(s);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }

  /**
   * Convert the given list of strings to a list of OffsetDateTimes. This method returns the result in a new List.
   * @param strings the list of strings to convert.
   * @return the given list of strings converted to a list of OffsetDateTimes.
   */
  public static List<OffsetDateTime> toOffsetDateTimeList(List<String> strings) {
    List<OffsetDateTime> dateTimes = new ArrayList<>(strings.size());
    for (String s : strings) {
      dateTimes.add(OffsetDateTime.parse(s));
    }
    return dateTimes;
  }

  /**
   * Convert the given list of strings to an array of OffsetDateTimes. This method returns the result in a new array.
   * @param strings the list of strings to convert.
   * @return the given list of strings converted to an array of OffsetDateTimes.
   */
  public static OffsetDateTime[] toOffsetDateTimeArray(List<String> strings) {
    OffsetDateTime[] dateTimes = new OffsetDateTime[strings.size()];
    for (int i = 0; i < dateTimes.length; i++) {
      dateTimes[i] = OffsetDateTime.parse(strings.get(i));
    }
    return dateTimes;
  }

  public static List<LocalDateTime> toLocalDateTimeList(List<String> strings) {
    List<LocalDateTime> dateTimes = new ArrayList<>(strings.size());
    for (String s : strings) {
      dateTimes.add(LocalDateTime.parse(s));
    }
    return dateTimes;
  }

  public static LocalDateTime[] toLocalDateTimeArray(List<String> strings) {
    LocalDateTime[] dateTimes = new LocalDateTime[strings.size()];
    for (int i = 0; i < dateTimes.length; i++) {
      dateTimes[i] = LocalDateTime.parse(strings.get(i));
    }
    return dateTimes;
  }

  static List<Boolean> toBooleanList(List<String> strings) {
    List<Boolean> booleans = new ArrayList<>(strings.size());
    for (String s : strings) {
      booleans.add(Boolean.parseBoolean(s));
    }
    return booleans;
  }

  static Boolean[] toBooleanArray(List<String> strings) {
    Boolean[] booleans = new Boolean[strings.size()];
    for (int i = 0; i < booleans.length; i++) {
      booleans[i] = Boolean.parseBoolean(strings.get(i));
    }
    return booleans;
  }
}
