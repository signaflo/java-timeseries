package math;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 26, 2017
 */
public interface Field<T> {

    T zero();
    T one();

    static RealField real() {
        return RealField.getInstance();
    }

    static RationalField rational() {
        return RationalField.getInstance();
    }
}

class RealField implements Field<Real> {

    private RealField() {}
    private static RealField realField;

    static RealField getInstance() {
        if (realField == null) {
            realField = new RealField();
            return realField;
        }
        return realField;
    }

    @Override
    public Real zero() {
        return Real.from(0.0);
    }

    @Override
    public Real one() {
        return Real.from(1.0);
    }
}

class RationalField implements Field<Rational> {

    private RationalField() {}
    private static RationalField rationalField;

    static RationalField getInstance() {
        if (rationalField == null) {
            rationalField = new RationalField();
            return rationalField;
        }
        return rationalField;
    }

    @Override
    public Rational zero() {
        return Rational.from(0);
    }

    @Override
    public Rational one() {
        return Rational.from(1);
    }
}
