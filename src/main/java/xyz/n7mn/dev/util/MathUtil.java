package xyz.n7mn.dev.util;

public class MathUtil {

    public static long multiply(long x, double y) {
        //精度は失うがこれが最適
        return Math.round((x * y));
    }

    public static long add(long x, long y, boolean maxValue) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException ex) {
            return maxValue ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
    }

    public static long subtract(long x, long y, boolean maxValue) {
        try {
            return Math.subtractExact(x, y);
        } catch (ArithmeticException ex) {
            return maxValue ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
    }
}