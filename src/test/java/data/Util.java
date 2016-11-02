package data;

/**
 * @author Jacob Rachiele
 *
 */
public final class Util {

  private Util() {}

  public static void printArray(double[] data) {
    System.out.print("(");
    for (int i = 0; i < data.length - 1; i++) {
      System.out.print(data[i] + ", ");
    }
    System.out.print(data[data.length - 1] + ")");
  }
}
