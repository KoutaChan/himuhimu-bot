package xyz.n7mn.dev.util.pair;

import java.io.Serializable;
import java.util.Objects;

/**
 * credit https://github.com/hiljusti/pair/blob/core/src/main/java/so/dang/cool/Pair.java
 */
public class Pair<L, R> implements Serializable {
    private static final long serialVersionUID = 6035080875813945322L;

    private L left;
    private R right;

    /**
     * Make a Pair of things.
     * @param left The left thing
     * @param right The right thing
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Make a Pair of things.
     * @param <L> The left type
     * @param <R> The right type
     * @param left The left thing
     * @param right The right thing
     * @return a new Pair
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    /**
     * Get the left value.
     * @return the left value
     */
    public L getLeft() {
        return left;
    }

    /**
     * Get the left value. An alias for the {@link #getLeft()} method, the
     * "getKey" convention is to support use cases where {@link Pair}
     * was used and may be difficult to untangle.
     * @return the left value
     */
    public L getKey() {
        return left;
    }

    public void setKey(L left) {
        this.left = left;
    }

    /**
     * Get the right value.
     * @return the right value
     */
    public R getRight() {
        return right;
    }

    /**
     * Get the right value. An alias for the {@link #getRight()} method, the
     * "getKey" convention is to support use cases where {@link Pair}
     * was used and may be difficult to untangle.
     * @return the right value
     */
    public R getValue() {
        return right;
    }

    public void setValue(R right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "[" +
                left +
                ", " +
                right +
                "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) { return true; }
        if (!(that instanceof Pair)) { return false; }

        Pair pair = (Pair) that;

        return Objects.equals(this.left, pair.getKey()) && Objects.equals(this.right, pair.getValue());
    }
}
