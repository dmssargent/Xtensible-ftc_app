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

package org.ftccommunity.i2clibrary.interfaces;

import org.ftccommunity.i2clibrary.navigation.Acceleration;
import org.ftccommunity.i2clibrary.navigation.Position;
import org.ftccommunity.i2clibrary.navigation.Velocity;

/**
 * {@link IAccelerationIntegrator} encapsulates an algorithm for integrating acceleration
 * information over time to produce velocity and position.
 *
 * @see IBNO055IMU
 */
public interface IAccelerationIntegrator {
    /**
     * Initializes the algorithm with a starting position and velocity. Any timestamps that are
     * present in these data are not to be considered as significant. The initial acceleration
     * should be taken as undefined; you should set it to null when this method is called.
     *
     * @param initialPosition the starting position
     * @param initialVelocity the starting velocity
     * @see #update(Acceleration)
     */
    void initialize(Position initialPosition, Velocity initialVelocity);

    /**
     * Returns the current position as calculated by the algorithm
     *
     * @return the current position
     */
    Position getPosition();

    /**
     * Returns the current velocity as calculated by the algorithm
     *
     * @return the current velocity
     */
    Velocity getVelocity();

    /**
     * Returns the current acceleration as understood by the algorithm. This is typically just the
     * value provided in the most recent call to {@link #update(Acceleration)}, if any.
     *
     * @return the current acceleration, or null if the current position is undefined
     */
    Acceleration getAcceleration();

    /**
     * Step the algorithm as a result of the stimulus of new acceleration data.
     *
     * @param acceleration the acceleration as just reported by the IMU
     */
    void update(Acceleration acceleration);
}
