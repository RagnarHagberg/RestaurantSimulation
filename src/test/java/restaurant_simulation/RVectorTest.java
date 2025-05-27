package restaurant_simulation;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RVectorTest {

    @Test
    public void testConstructorAndGetters() {
        RVector vector = new RVector(1.0, 2.0, 3.0);
        assertEquals(1.0, vector.getA());
        assertEquals(2.0, vector.getB());
        assertEquals(3.0, vector.getC());
    }


    @Test
    void testSubtractVectors() {
        RVector v1 = new RVector(2.0, -3.0, 4.0);
        RVector v2 = new RVector(-1.0, 2.0, -5.0);
        RVector result = v1.subtractVector(v2);

        assertEquals(3.0, result.getA());
        assertEquals(-5.0, result.getB());
        assertEquals(9.0, result.getC());
    }

    @Test
    void testSubtractZeroVector() {
        RVector v1 = new RVector(1.0, 2.0, 3.0);
        RVector v2 = new RVector(0.0, 0.0, 0.0);
        RVector result = v1.subtractVector(v2);

        assertEquals(1.0, result.getA());
        assertEquals(2.0, result.getB());
        assertEquals(3.0, result.getC());
    }

    @Test
    void testSubtractNaNValues() {
        RVector v1 = new RVector(Double.NaN, 1.0, 2.0);
        RVector v2 = new RVector(1.0, Double.NaN, 3.0);
        RVector result = v1.subtractVector(v2);

        assertTrue(Double.isNaN(result.getA()));
        assertTrue(Double.isNaN(result.getB()));
        assertEquals(-1.0, result.getC());
    }
    @Test
    public void testGetLength() {
        RVector vector = new RVector(3.0, -4.0, 0.0);
        assertEquals(5.0, vector.getLength());

    }

    @Test
    void testLengthUnitVectors() {
        assertEquals(1.0, new RVector(1.0, 0.0, 0.0).getLength());
        assertEquals(1.0, new RVector(0.0, 1.0, 0.0).getLength());
        assertEquals(1.0, new RVector(0.0, 0.0, 1.0).getLength());
        assertEquals(1.0, new RVector(-1.0, 0.0, 0.0).getLength());
    }


    @Test
    void testGetUnitVector() {
        RVector vector = new RVector(3.0, -4.0, 0.0);
        RVector unitVector = vector.getUnitVector();

        assertEquals(0.6, unitVector.getA());
        assertEquals(-0.8, unitVector.getB());
        assertEquals(0.0, unitVector.getC());
        assertEquals(1.0, unitVector.getLength());
    }

    @Test
    public void testGetUnitVectorZeroLengthThrowsException() {
        RVector zeroVector = new RVector(0.0, 0.0, 0.0);
        assertThrows(ArithmeticException.class, zeroVector::getUnitVector);
    }

    @Test
    public void testGetScaledVector() {
        RVector vector = new RVector(1.0, -2.0, 3.0);
        double scalar = 2.5;
        RVector scaled = vector.getScaledVector(scalar);
        assertEquals(1.0 * 2.5, scaled.getA(), 1e-9);
        assertEquals(-2.0 * 2.5, scaled.getB(), 1e-9);
        assertEquals(3.0 * 2.5, scaled.getC(), 1e-9);
    }

    @Test
    void testScaleByZero() {
        RVector vector = new RVector(1.0, 2.0, 3.0);
        RVector scaled = vector.getScaledVector(0.0);

        assertEquals(0.0, scaled.getA());
        assertEquals(0.0, scaled.getB());
        assertEquals(0.0, scaled.getC());
    }

    @Test
    void testScaleByInfinityScalar() {
        RVector vector = new RVector(1.0, 2.0, 3.0);
        RVector scaled = vector.getScaledVector(Double.POSITIVE_INFINITY);

        assertEquals(Double.POSITIVE_INFINITY, scaled.getA());
        assertEquals(Double.POSITIVE_INFINITY, scaled.getB());
        assertEquals(Double.POSITIVE_INFINITY, scaled.getC());
    }

    @Test
    void testScaleByNaNScalar() {
        RVector vector = new RVector(1.0, 2.0, 3.0);
        RVector scaled = vector.getScaledVector(Double.NaN);

        assertTrue(Double.isNaN(scaled.getA()));
        assertTrue(Double.isNaN(scaled.getB()));
        assertTrue(Double.isNaN(scaled.getC()));
    }

    @Test
    void testScalingPreservesDirection() {
        RVector original = new RVector(3.0, 4.0, 5.0);
        RVector scaled = original.getScaledVector(2.0);

        // Get unit vectors to compare directions
        RVector originalUnit = original.getUnitVector();
        RVector scaledUnit = scaled.getUnitVector();

        assertEquals(originalUnit.getA(), scaledUnit.getA());
        assertEquals(originalUnit.getB(), scaledUnit.getB());
        assertEquals(originalUnit.getC(), scaledUnit.getC());
    }

}
