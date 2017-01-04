package data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * A set of basic data types.
 */
enum DataType {

  BOOLEAN("Boolean") {
    @Override
    public Boolean convert(String s) {
      return Boolean.parseBoolean(s);
    }
    @Override
    public List<Boolean> convertList(List<String> strings) {
      return Types.toBooleanList(strings);
    }
    @Override
    public Boolean[] convert(List<String> strings) {
      return Types.toBooleanArray(strings);
    }
  },

  DOUBLE("Double") {
    @Override
    public Double convert(String s) {
      return Double.parseDouble(s);
    }
    @Override
    public List<Double> convertList(List<String> strings) {
      return Types.toDoubleList(strings);
    }

    @Override
    public Double[] convert(List<String> strings) {
      return Types.toDoubleArray(strings);
    }
  },

  LOCAL_DATE_TIME("LocalDateTime") {
    @Override
    public LocalDateTime convert(String s) {
      try {
        return LocalDateTime.parse(s);
      } catch (DateTimeParseException e) {
        System.out.println(e.getMessage());
      }
      return null;
    }
    @Override
    public List<LocalDateTime> convertList(List<String> strings) {
      return Types.toLocalDateTimeList(strings);
    }

    @Override
    public LocalDateTime[] convert(List<String> strings) {
      return Types.toLocalDateTimeArray(strings);
    }
  },

  OFFSET_DATE_TIME("OffsetDateTime") {
    @Override
    public OffsetDateTime convert(String s) {
      try {
        return OffsetDateTime.parse(s);
      } catch (DateTimeParseException e) {
        System.out.println(e.getMessage());
      }
      return null;
    }
    @Override
    public List<OffsetDateTime> convertList(List<String> strings) {
      return Types.toOffsetDateTimeList(strings);
    }

    @Override
    public OffsetDateTime[] convert(List<String> strings) {
      return Types.toOffsetDateTimeArray(strings);
    }
  },

  STRING("String") {
    @Override
    public String convert(String s) {
      return s;
    }
    @Override
    public List<String> convertList(List<String> strings) {
      return new ArrayList<>(strings);
    }

    @Override
    public String[] convert(List<String> strings) {
      return String[].class.cast(strings.toArray());
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

  public abstract List<?> convertList(List<String> strings);

  public abstract Object[] convert(List<String> strings);

  public abstract Object convert(String s);

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
