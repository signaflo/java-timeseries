package data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * 
 * @author Jacob Rachiele
 */
public final class CsvReader {
  
  private final File csvFile;
  private final CSVParser parser;
  private final List<List<String>> parsedRecords;
  private final List<String> header;
  
  public CsvReader(final String csvFilePath, final boolean headerRow) {
    this.csvFile = new File(getClass().getClassLoader().getResource(csvFilePath).getFile());
    this.parser = getParser(csvFile);
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
      this.header = new ArrayList<String>();
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
    return allRows;
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
  
  public final List<List<String>> getRows() {
    return this.parsedRecords;
  }
  
  public final List<String> getRow(final int i) {
    return this.parsedRecords.get(i);
  }
  
  public final List<String> getColumn(final int i) {
    List<String> column = new ArrayList<>(this.parsedRecords.size());
    for (List<String> record : parsedRecords) {
      column.add(record.get(i));
    }
    return column;
  }
  
  public final List<String> getHeader() {
    return this.header;
  }
  
  public final List<List<String>> getColumns() {
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
  }
  
}
