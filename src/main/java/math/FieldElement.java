package math;

/**
 * Represents an element of a mathematical <a target="_blank" 
 * href="https://en.wikipedia.org/wiki/Field_(mathematics)">field</a>.
 * @author Jacob Rachiele
 *
 * @param <T> The type of field element.
 */
public interface FieldElement<T> {

  /**
   * Add this element to the given element.
   * @param other the element to add to this element.
   * @return this element added to the given element.
   */
  T plus(T other);
  
  /**
   * Subtract the given element from this element.
   * @param other the element to subtract from this element.
   * @return this element subtracted by the given element.
   */
  T minus(T other);
  
  /**
   * Multiply this element by the given element.
   * @param other the element to multiply this element by.
   * @return this element multiplied by the given element.
   */
  T times(T other);
  
  /**
   * The square root operation applied to this element.
   * @return the square root of this element.
   */
  T sqrt();
  
  /**
   * Compute and return the conjugate of this element.
   * @return the conjugate of this element.
   */
  T conjugate();
  
  /**
   * Compute and return the absolute value of this element.
   * @return the absolute value of this element.
   */
  double abs();
  
}
