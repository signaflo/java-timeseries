package optim.doubles;

import org.junit.Test;

import optim.AbstractFunction;

public final class WolfePowellLineSearchSpec {
  
  @Test
  public void testLineSearch() {
    AbstractFunction f = new TestFunction1();
    final double f0 = f.at(0);
    final double slope0 = f.slopeAt(0);
    final double c1 = 1E-3;
    final double c2 = 0.1;
    WolfePowellLineSearch lineSearch = new WolfePowellLineSearch(f, c1, c2, f0, slope0);
    System.out.println(lineSearch.search());
    System.out.println(f.functionEvaluations);
    System.out.println(f.slopeEvaluations);
  }

}
