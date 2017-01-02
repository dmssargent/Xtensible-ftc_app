package org.ftccommunity.ftcxtensible.collections;

/**
 * Created by mhsrobotics on 10/27/16.
 */

public class TripleAxis<T> {
    public final T X;
    public final T Y;
    public final T Z;

    public TripleAxis(T x, T y, T z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
