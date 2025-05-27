package restaurant_simulation;

/**
 * Represents a 3D vector with components a, b, and c.
 */
public class RVector {

    private double a;
    private double b;
    private double c;

    /**
     * Constructs a 3D vector with the specified components.
     *
     * @param a the x-component
     * @param b the y-component
     * @param c the z-component
     */
    RVector(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    /**
     * Returns a new vector resulting from subtracting the given vector from this vector.
     *
     * @param otherRVector the vector to subtract
     * @return a new vector representing the difference
     */
    public RVector subtractVector(RVector otherRVector) {
        return new RVector(a - otherRVector.getA(), b - otherRVector.getB(), c - otherRVector.getC());
    }

    /**
     * Calculates the Euclidean length (magnitude) of the vector.
     *
     * @return the vector's length
     */
    public double getLength() {
        return Math.sqrt(a * a + b * b + c * c);
    }

    /**
     * Returns the unit vector in the same direction as this vector.
     *
     * @return a new vector representing the unit vector
     * @throws ArithmeticException if the vector's length is zero
     */
    public RVector getUnitVector() {
        double length = getLength();
        if (length == 0) {
            throw new ArithmeticException("Cannot create unit vector from zero-length vector.");
        }
        return new RVector(a / length, b / length, c / length);
    }

    /**
     * Returns a new vector scaled by the given scalar.
     *
     * @param scalar the factor by which to scale the vector
     * @return a new scaled vector
     */
    public RVector getScaledVector(double scalar) {
        return new RVector(a * scalar, b * scalar, c * scalar);
    }
}
