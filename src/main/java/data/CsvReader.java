package data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
public final class CsvReader {
  
  private final File csvFile;
  private final CSVParser parser;
  private final List<List<String>> parsedRecords;
  
  public CsvReader(final String csvFilePath, final boolean header) {
    this.csvFile = new File(getClass().getClassLoader().getResource(csvFilePath).getFile());
    this.parser = getParser(csvFile);
    List<CSVRecord> records = null;
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
    if (header) records.remove(0);
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
  
  public final List<List<String>> parsedRecords() {
    return this.parsedRecords;
  }
  
}
