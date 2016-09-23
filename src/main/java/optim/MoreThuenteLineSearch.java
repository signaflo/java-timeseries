package optim;

final class MoreThuenteLineSearch {
  
  private static final double alphaMin = 1E-3;
  private static final double alphaMax = 1.0;
  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;
  
  private final AbstractFunction phi;
  private final double phiAt0;
  private final double slopeAt0;
  private final Function psi;
  private final Function psiSlope;
  
  MoreThuenteLineSearch(final AbstractFunction f) {
    this.phi = f;
    this.phiAt0 = f.at(0);
    this.slopeAt0 = phi.slopeAt(0);
    this.psi = (alpha) -> f.at(alpha) - phiAt0 - c1 * alpha * slopeAt0;
    this.psiSlope = (alpha) -> f.slopeAt(alpha) - c1 * slopeAt0;
  }
  
  public final void search() {
    int k = 0;
    while (!converged()) {
      // Choose a safeguarded alpha in interval k
      // Test for convergence
      // Update the interval
    }
  }
  
  private final boolean converged() {
    return false;
  }
  
  private final void updateInterval() {
    double alphaLower = 0.01;
    double alphaUpper = 0.99;
    
  }

}
