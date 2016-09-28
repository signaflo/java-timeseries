package optim.doubles;

import org.junit.Test;

import optim.AbstractFunction;

public final class WolfePowellLineSearchSpec {
  
  @Test
  public void testLineSearch() {
    AbstractFunction f = new TestFunction2();
    final double f0 = f.at(0);
    final double slope0 = f.slopeAt(0);
    final double c1 = 0.1;
    final double c2 = 0.1;
    WolfePowellLineSearch lineSearch = WolfePowellLineSearch.newBuilder(f, f0, slope0)
            .c1(c1).c2(c2).alphaMax(20.0).alpha0(2.5).build();
    System.out.println(lineSearch.search());
    System.out.println(f.functionEvaluations);
    System.out.println(f.slopeEvaluations);
  }

}
