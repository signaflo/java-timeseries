package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

final class CsvReader {
  
  private final File csvFile;
  private final CSVParser parser;
  
  CsvReader(final String csvFilePath) {
    this.csvFile = new File(getClass().getClassLoader().getResource(csvFilePath).getFile());
    this.parser = getParser(csvFile);
    
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
