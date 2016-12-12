package data;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by jacob on 12/10/16.
 */
public enum DataType {

  BOOLEAN("Boolean") {
    @Override
    Column convert(List<String> strings) {
      return new Column(strings, this);
    }
    @Override
    Boolean convert(String s) {
      return Boolean.parseBoolean(s);
    }
    @Override
    List<Boolean> convertList(List<String> strings) {
      return TypeConversion.toBooleanList(strings);
    }
  },

  DOUBLE("Double") {
    @Override
    Column convert(List<String> strings) {
      return new Column(strings, DataType.DOUBLE);
    }
    @Override
    Double convert(String s) {
      return Double.parseDouble(s);
    }
    @Override
    List<Double> convertList(List<String> strings) {
      return TypeConversion.toDoubleList(strings);
    }
  },

  LOCAL_DATE_TIME("LocalDateTime") {
    @Override
    Column convert(List<String> strings) {
      return new Column(strings, DataType.LOCAL_DATE_TIME);
    }
    @Override
    LocalDateTime convert(String s) {
      try {
        return LocalDateTime.parse(s);
      } catch (DateTimeParseException e) {
        System.out.println(e.getMessage());
      }
      return null;
    }
    @Override
    List<LocalDateTime> convertList(List<String> strings) {
      return TypeConversion.toLocalDateTimeList(strings);
    }
  },

  OFFSET_DATE_TIME("OffsetDateTime") {
    @Override
    Column convert(List<String> strings) {
      return new Column(strings, DataType.OFFSET_DATE_TIME);
    }
    @Override
    OffsetDateTime convert(String s) {
      try {
        return OffsetDateTime.parse(s);
      } catch (DateTimeParseException e) {
        System.out.println(e.getMessage());
      }
      return null;
    }
    @Override
    List<OffsetDateTime> convertList(List<String> strings) {
      return TypeConversion.toOffsetDateTimeList(strings);
    }
  },

  STRING("String") {
    @Override
    Column convert(List<String> string) {
      return new Column(string, DataType.STRING);
    }
    @Override
    String convert(String s) {
      return s;
    }
    @Override
    List<String> convertList(List<String> strings) {
      return new ArrayList<>(strings);
    }
  };

  private final String symbol;

  DataType(final String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }

  abstract List<?> convertList(List<String> strings);
  abstract Column convert(List<String> strings);
  abstract Object convert(String s);

  private static final Map<String, DataType> stringToEnum = new HashMap<>();

  static {
    for (DataType dt : values()) {
      stringToEnum.put(dt.toString(), dt);
    }
  }

  public static DataType fromString(String symbol) {
    return stringToEnum.get(symbol);
  }
}
