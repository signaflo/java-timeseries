package data;

import static data.DoubleFunctions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import stats.Statistics;

public class DoubleFunctionSpec {
  
  @Test
  public void whenNewArrayCreatedOutputValuesEqualToInputValues() {
    double[] expected = new double[] {3.0, 7.5, 10.0};
    double[] actual = newArray(3.0, 7.5, 10.0);
    assertThat(actual, is(equalTo(expected))); 
  }
  
  @Test
  public void whenNewArrayCreatedWithEmptyArgumentListEmptyArrayReturned() {
    double[] expected = new double[] {};
    double[] actual = newArray();
    assertThat(actual, is(equalTo(expected)));
  }
  
  @Test
  public void whenFillMethodThenArrayFilledWithGivenValue() {
    double[] expected = new double[] {1.0, 1.0, 1.0, 1.0, 1.0};
    double[] actual = fill(5, 1.0);
    assertThat(actual, is(equalTo(expected)));
  }
  
  @Test
  public void whenArraySlicedValuesWithinSliceRangeReturned() {
    double[] expected = new double[] {2.0, 4.5, -3.0, 9.2, 2.1};
    double[] data = newArray(1.3, 7.0, 2.0, 4.5, -3.0, 9.2, 2.1, 7.4);
    double[] actual = slice(data, 2, 7);
    assertThat(actual, is(equalTo(expected)));
  }
  
  @Test
  public void whenSqrtMethodThenSqrtOfEachValueReturned() {
    double[] data = newArray(1.0, 4.0, 9.0, 16.0);
    double[] expected = newArray(1.0, 2.0, 3.0, 4.0);
    assertThat(sqrt(data), is(equalTo(expected)));
  }
  
  @Test
  public void whenDataMeanRemovedThenResultMeanZero() {
    double[] data = TestData.internetTraffic().data();
    assertThat(Statistics.meanOf(demean(data)), is(closeTo(0.0, 1E-10)));
  }
  
  @Test
  public void whenBoxCoxThenTransformationApplied() {
    double[] data = TestData.debitcards().data();
    double[] transformed = boxCox(data, 0.5);
    assertThat(Statistics.meanOf(transformed), is(closeTo(244.1229, 1E-4)));
    assertThat(Statistics.stdDeviationOf(transformed), is(closeTo(38.60176, 1E-4)));
  }
  
  @Test
  public void whenInvBoxCoxThenTransformationUnapplied() {
    double[] transformed = boxCox(TestData.debitcards().data(), 0.5);
    double[] original = inverseBoxCox(transformed, 0.5);
    assertThat(Statistics.meanOf(original), is(closeTo(15514.25641, 1E-4)));
    assertThat(Statistics.stdDeviationOf(original), is(closeTo(4688.38717, 1E-4)));
  }
  
  @Test
  public void whenNegativeOfThenNegativeOfDataApplied() {
    double[] data = TestData.debitcards().data();
    double[] neg = negativeOf(data);
    assertThat(Statistics.meanOf(neg), is(closeTo(-15514.25641, 1E-4)));
    assertThat(Statistics.stdDeviationOf(neg), is(closeTo(4688.38717, 1E-4)));
  }
}
