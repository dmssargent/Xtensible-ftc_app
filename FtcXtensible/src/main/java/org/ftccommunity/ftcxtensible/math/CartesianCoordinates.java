/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.math;

import com.google.common.annotations.Beta;

/**
 * A representation of a point in the Cartesian coordinate system
 *
 * @author David
 * @since 0.2.1
 */
@Beta
public class CartesianCoordinates {
    private double x;
    private double y;

    /**
     * Builds a Cartesian point for the given point
     *
     * @param x the x for the point
     * @param y the y for the point
     */
    public CartesianCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Builds a Cartesian point for the given point
     *
     * @param x the x for the point
     * @param y the y for the point
     */
    public CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Duplicates an existing Cartesian point
     *
     * @param coordinates the {@code CartesianCoordinates} to copy from
     */
    public CartesianCoordinates(CartesianCoordinates coordinates) {
        x = coordinates.x;
        y = coordinates.y;
    }

    /**
     * Duplicates an existing Polar point
     *
     * @param coordinates the {@link PolarCoordinates} to copy from
     */
    public CartesianCoordinates(PolarCoordinates coordinates) {
        double r = coordinates.getR();
        double theta = coordinates.getTheta();

        x = r * Math.cos(theta);
        y = r * Math.sin(theta);
    }

    /**
     * Gets the current X-coordinate
     *
     * @return X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the current Y-coordinate
     *
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Reflect the point over the y=x axis
     *
     * @return this object after being reflected
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public CartesianCoordinates invert() {
        x = this.y;
        y = this.x;

        return this;
    }
}
