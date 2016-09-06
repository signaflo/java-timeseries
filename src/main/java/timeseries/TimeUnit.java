package timeseries;

import java.time.Duration;

public final class TimeUnit {
  
  private final TimeScale timeScale;
  private final int unitLength;
  
  public TimeUnit(final TimeScale timeScale, final int unitLength) {
    this.timeScale = timeScale;
    this.unitLength = unitLength;
  }
  
  public final TimeScale timeScale() {
    return this.timeScale;
  }
  
  public final int unitLength() {
    return this.unitLength;
  }
  
  /**
   * The total amount of time in this time scale measured in seconds, the base SI unit of time.
   * @return the total amount of time in this time scale measured in seconds.
   */
  double totalDuration() {
    
    double thisDuration = this.timeScale.totalDuration();
    
    // Since the duration is measured in seconds and is treated by the Duration class as a long, we need
    //     to treat time scales less than one second as special cases and return the values ourselves.
    switch (this.timeScale) {
      case NANOSECOND:
        return 1E-9 * this.unitLength;
      case MICROSECOND:
        return 1E-6 * this.unitLength;
      case MILLISECOND:
        return 1E-3 * this.unitLength;
      default:
        return thisDuration * this.unitLength;
    }
    
  }

}
