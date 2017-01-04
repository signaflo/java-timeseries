/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data from a CSV file.
 *
 * @author Jacob Rachiele
 */
final class CsvData {

  private final File csvFile;
  private final List<CSVRecord> parsedRecords;
  private final List<String> header;

  /**
   * Create a new CSV data object using the given file path. This constructor assumes no header row is present.
   *
   * @param csvFilePath the path to the file.
   */
  CsvData(final String csvFilePath) {
    this(csvFilePath, false);
  }

  /**
   * Create a new CSV data object using the given file path and header flag.
   *
   * @param csvFilePath the path to the file.
   * @param headerRow   indicates whether the file contains a header row.
   */
  CsvData(final String csvFilePath, final boolean headerRow) {
    URL resource = getClass().getClassLoader().getResource(csvFilePath);
    if (resource == null) {
      throw new IllegalArgumentException("The resource given by " + csvFilePath + " could not be found on the file system.");
    } else {
      this.csvFile = new File(resource.getFile());
    }
    final CSVParser parser = getParser(csvFile);
    List<CSVRecord> records;
    if (parser == null) {
      throw new IllegalArgumentException("The resource given by " + csvFilePath + " could not be found on the file system.");
    } else {
      try {
        records = parser.getRecords();
      } catch (IOException ie) {
        throw new RuntimeException("The csv parser failed to retrieve the records.");
      } finally {
        try {
          parser.close();
        } catch (IOException ie) {
          ie.printStackTrace();
        }
      }
      // TODO: Add method for large csv files where we don't read all records at once.
      if (headerRow) {
        this.header = new ArrayList<>();
        CSVRecord headerRecord = records.remove(0);
        for (String s : headerRecord) {
          this.header.add(s);
        }
      } else {
        this.header = Collections.emptyList();
      }
      this.parsedRecords = records;
    }
  }

  private CSVParser getParser(final File csvFile) {
    try {
      return CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
    } catch (IOException ie) {
      ie.printStackTrace();
      System.out.println("Error parsing csv file. Path : " + csvFile.getAbsolutePath());
      return null;
    }
  }

  /**
   * The csv file.
   *
   * @return the csv file.
   */
  File csvFile() {
    return this.csvFile;
  }

  /**
   * The data row-by-row.
   *
   * @return the data row-by-row.
   */
  List<CSVRecord> getRows() {
    return this.parsedRecords;
  }

  /**
   * The ith row of data.
   *
   * @param i the index of the row.
   * @return the ith row of data.
   */
  CSVRecord getRow(final int i) {
    return this.parsedRecords.get(i);
  }

  /**
   * The ith column of data.
   *
   * @param i the index of the column.
   * @return the ith column of data.
   */
  List<String> getColumn(final int i) {
    List<String> column = new ArrayList<>(this.parsedRecords.size());
    for (CSVRecord record : parsedRecords) {
      column.add(record.get(i));
    }
    return column;
  }

  /**
   * The header row.
   *
   * @return the header row.
   */
  List<String> getHeader() {
    return Collections.unmodifiableList(this.header);
  }

  /**
   * The data column-by-column.
   *
   * @return the data column-by-column.
   */
  List<List<String>> getColumns() {
    int nCols = this.parsedRecords.get(0).size();
    List<List<String>> columns = new ArrayList<>(nCols);
    List<String> column;
    for (int i = 0; i < nCols; i++) {
      column = new ArrayList<>(this.parsedRecords.size());
      for (CSVRecord row: this.parsedRecords) {
        column.add(row.get(i));
      }
      columns.add(column);
    }
    return columns;
    //return Collections.unmodifiableList(columns);
  }

  /**
   * The column with the given name.
   *
   * @param columnName the name of the column.
   * @return the column with the given name.
   */
  List<String> getColumn(final String columnName) {
    List<String> headerRow = getHeader();
    for (int i = 0; i < headerRow.size(); i++) {
      if (headerRow.get(i).equals(columnName)) {
        return getColumn(i);
      }
    }
    throw new IllegalArgumentException("A column with the name " + columnName + " was not found.");
  }

  /**
   * Create a new dataframe with the column types set according to the given schema.
   * @param schema a list of data types for each column in the dataframe.
   * @return a new dataframe with the column types set according to the given schema.
   */
   DataFrame createDataFrame(Schema schema) {
    List<List<String>> columns = getColumns();
    if (schema.size() != columns.size()) {
      throw new IllegalArgumentException("The schema size was " + schema.size() + " but the number of columns was " +
          columns.size() + ". There must be one data type for each column.");
    }
    DataFrame df = new DataFrame(schema.size());
    int colId = 0;
    String stringColId;
    DataType type;
    List<String> column;
    List<String> header = this.getHeader();
    for (int i = 0; i < schema.size(); i++) {
      type = schema.get(i);
      stringColId = (header.isEmpty())? Integer.toString(colId++) : header.get(i);
      column = columns.get(i);
      switch (type) {
        case DOUBLE:
          Double[] doubles = Types.toDoubleArray(column);
          df.add(stringColId, Double[].class, doubles);
          break;
        case BOOLEAN:
          Boolean[] booleans = Types.toBooleanArray(column);
          df.add(stringColId, Boolean[].class, booleans);
          break;
        case LOCAL_DATE_TIME:
          LocalDateTime[] localDateTimes = Types.toLocalDateTimeArray(column);
          df.add(stringColId, LocalDateTime[].class, localDateTimes);
          break;
        case OFFSET_DATE_TIME:
          OffsetDateTime[] offsetDateTimes = Types.toOffsetDateTimeArray(column);
          df.add(stringColId, OffsetDateTime[].class, offsetDateTimes);
          break;
        case STRING:
          df.add(stringColId, String[].class, Types.toStringArray(column));
          break;
        default:
          df.add(stringColId, String[].class, Types.toStringArray(column));
      }
    }
    return df;
  }

  /**
   * Create a dataframe from this csv data with each column type set to String.
   * @return a new dataframe from this csv data with each column type set to String.
   */
  DataFrame createDataFrame() {
    List<List<String>> columns = getColumns();
    DataFrame df = new DataFrame(columns.size());
    int colId = 0;
    String stringColId;
    List<String> column;
    List<String> header = this.getHeader();
    for (int i = 0; i < columns.size(); i++) {
      stringColId = (header.isEmpty())? Integer.toString(colId++) : header.get(i);
      column = columns.get(i);
      df.add(stringColId, String[].class, Types.toStringArray(column));
    }
    return df;
  }


}
