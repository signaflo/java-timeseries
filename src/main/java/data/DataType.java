package data;

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
    public Column convert(List<String> strings) {
      return new Column(strings, this);
    }
    @Override
    public Boolean convert(String s) {
      return Boolean.parseBoolean(s);
    }
    @Override
    public List<Boolean> convertList(List<String> strings) {
      return TypeConversion.toBooleanList(strings);
    }
  },

  DOUBLE("Double") {
    @Override
    public Column convert(List<String> strings) {
      return new Column(strings, DataType.DOUBLE);
    }
    @Override
    public Double convert(String s) {
      return Double.parseDouble(s);
    }
    @Override
    public List<Double> convertList(List<String> strings) {
      return TypeConversion.toDoubleList(strings);
    }
  },

  LOCAL_DATE_TIME("LocalDateTime") {
    @Override
    public Column convert(List<String> strings) {
      return new Column(strings, DataType.LOCAL_DATE_TIME);
    }
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
      return TypeConversion.toLocalDateTimeList(strings);
    }
  },

  OFFSET_DATE_TIME("OffsetDateTime") {
    @Override
    public Column convert(List<String> strings) {
      return new Column(strings, DataType.OFFSET_DATE_TIME);
    }
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
      return TypeConversion.toOffsetDateTimeList(strings);
    }
  },

  STRING("String") {
    @Override
    public Column convert(List<String> string) {
      return new Column(string, DataType.STRING);
    }
    @Override
    public String convert(String s) {
      return s;
    }
    @Override
    public List<String> convertList(List<String> strings) {
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

  public abstract List<?> convertList(List<String> strings);
  public abstract Column convert(List<String> strings);
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
