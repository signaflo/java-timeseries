package data;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jacob on 12/13/16.
 */
public class RowSpec {

  @Test
  public void putRowData() throws Exception {
    Row row = new Row();
    row.put(Double.class, 3.0);
    Double x = row.get(0, Double.class);
    System.out.println(x);
  }

  @Test
  public void getRow() throws Exception {

  }

}