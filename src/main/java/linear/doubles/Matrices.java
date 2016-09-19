package linear.doubles;

public final class Matrices {
  
  private Matrices(){}
  
  public static final Matrix identity(final int n) {
    final double[] data = new double[n * n];
    for (int i = 0; i < n; i++) {
      data[i * n + i] = 1.0;
    }
    return new Matrix(n, n , data);
  }

}
