package data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * 
 * @author Jacob Rachiele
 *
 * @param <T> The type of the first column.
 * @param <V> The type of the columns following the first.
 */
final class CsvReader<T, V> {
  
  private final File csvFile;
  private final CSVParser parser;
  
  CsvReader(final String csvFilePath, Class<T> column1Class, Class<V> column2Class) {
    this.csvFile = new File(getClass().getClassLoader().getResource(csvFilePath).getFile());
    this.parser = getParser(csvFile);
    List<CSVRecord> records = null;
    try {
        records = parser.getRecords();
    } catch (IOException ie) {
      throw new RuntimeException("The csv parser failed to retrieve the records.");
    }
    List<T> firstColumnList = new ArrayList<>(records.size());
    List<V> secondColumnList = new ArrayList<>(records.size());
    // TODO: Add method for large csv files where we don't read all records at once.
    parseRecords(records, firstColumnList, secondColumnList, column1Class, column2Class);

  }
  
  private void parseRecords(List<CSVRecord> records, List<T> firstColumnList, List<V> secondColumnList,
      Class<T> column1Class, Class<V> column2Class) {
    for (CSVRecord record : records) {
      if (record.size() > 0) {
        String column1String = record.get(0);
        Method[] column1Methods = column1Class.getDeclaredMethods();
        for (Method m : column1Methods) {
          if (m.getName().equalsIgnoreCase("parse") && m.getParameterCount() == 1 &&
              m.getParameterTypes()[0] == CharSequence.class) {
            try {
              firstColumnList.add(column1Class.cast(m.invoke(column1Class, column1String)));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              throw new IllegalArgumentException("The column1Class type, " + column1Class.getName() + 
                  ", is not supported.");
            }
          }
        }
      }
      if (record.size() > 1) {
        String column2String = record.get(1);
        Method[] column2Methods = column2Class.getDeclaredMethods();
        for (Method m : column2Methods) {
          if (m.getName().equalsIgnoreCase("valueOf") && m.getParameterCount() == 1 &&
              m.getParameterTypes()[0] == String.class) {
            try {
              secondColumnList.add(column2Class.cast(m.invoke(column2Class, column2String)));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              throw new IllegalArgumentException("The class type " + column2Class.getName() + 
                  " is not supported. The class should be a subtype of java.lang.Number.");
            }
          }
        }
      }
    }
  }

  private CSVParser getParser(final File csvFile) {
    try {
      return CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
    }
    catch (IOException ie) {
      ie.printStackTrace();
      System.out.println("Error parsing csv file. Path : " + csvFile.getAbsolutePath());
      return null;
    }
  }
  
  public final File csvFile() {
    return this.csvFile;
  }
  
  public final CSVParser parser() {
    return this.parser;
  }
  
}
