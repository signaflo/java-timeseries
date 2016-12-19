package optim;

/**
 * Created by jacob on 12/17/16.
 */
public class NaNStepLengthException extends ArithmeticException{

  public NaNStepLengthException(String message) {
    super(message);
  }
}
