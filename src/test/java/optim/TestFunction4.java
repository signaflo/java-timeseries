package optim;

final class TestFunction4 extends AbstractFunction {

  @Override
  public final double at(final double point) {
    return -point / (point * point + 2.0);
  }
  
  @Override
  public final double slopeAt(final double point) {
    return (point * point - 2) / Math.pow((point * point + 2), 2);
  }

}
