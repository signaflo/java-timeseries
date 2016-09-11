package math;

public interface FieldElement<T> {

  T plus(T other);
  
  T times(T other);
}
