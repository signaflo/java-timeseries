package data;

/**
 * Created by jacob on 12/4/16.
 */
public class NotADoubleException extends RuntimeException {

  public NotADoubleException() {
    this("An attempt was made to treat a non-Double object as a Double.");
  }

  public NotADoubleException(String message) {
    super(message);
  }
}
