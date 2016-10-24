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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for reading data from CSV files.
 *
 * @author Jacob Rachiele
 */
final class CsvReader {

  private final File csvFile;
  private final List<List<String>> parsedRecords;
  private final List<String> header;

  /**
   * Create a new CSV reader using the given file path. This constructor assumes no header row is present.
   *
   * @param csvFilePath the path to the file.
   */
  CsvReader(final String csvFilePath) {
    this(csvFilePath, false);
  }

  /**
   * Create a new CSV reader using the given file path and header flag.
   *
   * @param csvFilePath the path to the file.
   * @param headerRow   indicates whether the file contains a header row.
   */
  CsvReader(final String csvFilePath, final boolean headerRow) {
    this.csvFile = new File(getClass().getClassLoader().getResource(csvFilePath).getFile());
    final CSVParser parser = getParser(csvFile);
    List<CSVRecord> records = Collections.emptyList();
    try {
      records = parser.getRecords();
    } catch (IOException ie) {
      throw new RuntimeException("The csv parser failed to retrieve the records.");
    } finally {
      try {
        parser.close();
      } catch (IOException ie) {
        throw new RuntimeException("Failure closing CSVParser.");
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
    this.parsedRecords = parseRecords(records);

  }

  private List<List<String>> parseRecords(List<CSVRecord> records) {
    List<String> row;
    List<List<String>> allRows = new ArrayList<>(records.size());
    for (CSVRecord record : records) {
      row = new ArrayList<>(record.size());
      for (String s : record) {
        row.add(s);
      }
      allRows.add(row);
    }
    return Collections.unmodifiableList(allRows);
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
  final File csvFile() {
    return this.csvFile;
  }

  /**
   * The data row-by-row.
   *
   * @return the data row-by-row.
   */
  final List<List<String>> getRows() {
    return this.parsedRecords;
  }

  /**
   * The ith row of data.
   *
   * @param i the index of the row.
   * @return the ith row of data.
   */
  final List<String> getRow(final int i) {
    return this.parsedRecords.get(i);
  }

  /**
   * The ith column of data.
   *
   * @param i the index of the column.
   * @return the ith column of data.
   */
  final List<String> getColumn(final int i) {
    List<String> column = new ArrayList<>(this.parsedRecords.size());
    for (List<String> record : parsedRecords) {
      column.add(record.get(i));
    }
    return column;
  }

  /**
   * The header row.
   *
   * @return the header row.
   */
  final List<String> getHeader() {
    return Collections.unmodifiableList(this.header);
  }

  /**
   * The data column-by-column.
   *
   * @return the data column-by-column.
   */
  final List<List<String>> getColumns() {
    int nCols = this.parsedRecords.get(0).size();
    List<List<String>> columns = new ArrayList<>(nCols);
    List<String> column;
    for (int i = 0; i < nCols; i++) {
      column = new ArrayList<>(this.parsedRecords.size());
      for (List<String> row : this.parsedRecords) {
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
  final List<String> getColumn(final String columnName) {
    List<String> headerRow = getHeader();
    for (int i = 0; i < headerRow.size(); i++) {
      if (headerRow.get(i).equals(columnName)) {
        return getColumn(i);
      }
    }
    throw new IllegalArgumentException("A column with the provided column name was not found.");
  }

}
