package math;

public interface FieldElement<T> {

  T plus(T other);
  
  T minus(T other);
  
  T times(T other);
  
  T sqrt();
  
  T conjugate();
  
  double abs();
  
}
