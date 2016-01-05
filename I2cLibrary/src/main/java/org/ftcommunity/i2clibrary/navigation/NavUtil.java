/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ftcommunity.i2clibrary.navigation;

/**
 * NavUtil is a collection of utilities that provide useful manipulations of objects related to
 * navigation. It is typically most convenient to import these functions statically using: <p>
 * <pre>import static org.swerverobotics.library.interfaces.NavUtil.*;</pre>
 */
public final class NavUtil {
    //----------------------------------------------------------------------------------------------
    // Arithmetic: some handy helpers
    //----------------------------------------------------------------------------------------------

    public static Position plus(Position a, Position b) {
        return new Position(a.x + b.x, a.y + b.y, a.z + b.y, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Velocity plus(Velocity a, Velocity b) {
        return new Velocity(a.velocX + b.velocX, a.velocY + b.velocY, a.velocZ + b.velocZ, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Acceleration plus(Acceleration a, Acceleration b) {
        return new Acceleration(a.accelX + b.accelX, a.accelY + b.accelY, a.accelZ + b.accelZ, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Position minus(Position a, Position b) {
        return new Position(a.x - b.x, a.y - b.y, a.z - b.y, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Velocity minus(Velocity a, Velocity b) {
        return new Velocity(a.velocX - b.velocX, a.velocY - b.velocY, a.velocZ - b.velocZ, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Acceleration minus(Acceleration a, Acceleration b) {
        return new Acceleration(a.accelX - b.accelX, a.accelY - b.accelY, a.accelZ - b.accelZ, Math.max(a.nanoTime, b.nanoTime));
    }

    public static Position scale(Position p, double scale) {
        return new Position(p.x * scale, p.y * scale, p.z * scale, p.nanoTime);
    }

    public static Velocity scale(Velocity v, double scale) {
        return new Velocity(v.velocX * scale, v.velocY * scale, v.velocZ * scale, v.nanoTime);
    }

    public static Acceleration scale(Acceleration a, double scale) {
        return new Acceleration(a.accelX * scale, a.accelY * scale, a.accelZ * scale, a.nanoTime);
    }

    public static Position integrate(Velocity v, double dt) {
        return new Position(v.velocX * dt, v.velocY * dt, v.velocZ * dt, v.nanoTime);
    }

    //----------------------------------------------------------------------------------------------
    // Integration
    //----------------------------------------------------------------------------------------------

    public static Velocity integrate(Acceleration a, double dt) {
        return new Velocity(a.accelX * dt, a.accelY * dt, a.accelZ * dt, a.nanoTime);
    }

    /**
     * Integrate between two velocities to determine a change in position using an assumption that
     * the mean of the velocities has been acting the entire interval.
     *
     * @param cur  the current velocity
     * @param prev the previous velocity
     * @return an approximation to the change in position over the interval
     * @see <a href="https://en.wikipedia.org/wiki/Simpson%27s_rule">Simpson's Rule</a>
     */
    public static Position meanIntegrate(Velocity cur, Velocity prev) {
        double duration = (cur.nanoTime - prev.nanoTime) * 1e-9;
        Velocity meanVelocity = scale(plus(cur, prev), 0.5);
        return integrate(meanVelocity, duration);
    }

    /**
     * Integrate between two accelerations to determine a change in velocity using an assumption
     * that the mean of the accelerations has been acting the entire interval.
     *
     * @param cur  the current acceleration
     * @param prev the previous acceleration
     * @return an approximation to the change in velocity over the interval
     * @see <a href="https://en.wikipedia.org/wiki/Simpson%27s_rule">Simpson's Rule</a>
     */
    public static Velocity meanIntegrate(Acceleration cur, Acceleration prev) {
        double duration = (cur.nanoTime - prev.nanoTime) * 1e-9;
        Acceleration meanAcceleration = scale(plus(cur, prev), 0.5);
        return integrate(meanAcceleration, duration);
    }
}
