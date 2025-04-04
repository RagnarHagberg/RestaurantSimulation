package restaurant_simulation;

public class RVector {

    private double a;
    private double b;
    private double c;

    RVector(double a, double b, double c){
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

    public RVector subtractVector(RVector otherRVector){
        return new RVector(a - otherRVector.getA(), b - otherRVector.getB(), c - otherRVector.getC());
    }

    public double getLength(){
        return Math.pow(Math.pow(a,2) + Math.pow(b,2) + Math.pow(c,2), 0.5);
    }

    public RVector getUnitVector(){
        return new RVector(a/getLength(), b/getLength(), c/getLength());
    }

    public RVector getScaledVector(double scalar){
        return new RVector(a*scalar,b*scalar, c*scalar);
    }
}
