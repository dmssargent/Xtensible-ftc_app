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
package org.ftccommunity.ftcxtensible.math;

/**
 * A representation of a point in the Cartesian coordinate system
 *
 * @author David
 * @since 0.2.1
 */
public class PolarCoordinates {
    private double r;
    private double theta;

    /**
     * Builds an coordinate object based on a certain point
     *
     * @param r     the distance from the origin
     * @param theta the rotation in radians from the origin axis to the point
     */
    public PolarCoordinates(int r, int theta) {
        this.r = r;
        this.theta = theta;
    }

    /**
     * Builds an coordinate object based on a certain point
     *
     * @param r     the distance from the origin
     * @param theta the rotation in radians from the origin axis to the point
     */
    public PolarCoordinates(double r, double theta) {
        this.r = r;
        this.theta = theta;
    }

    /**
     * Builds a polar coordinate system from a cartesian coordinates
     *
     * @param coordinates the Cartesian coordinates of a point
     */
    public PolarCoordinates(CartesianCoordinates coordinates) {
        getCoordinates(coordinates);
    }

    /**
     * Builds a polar coordinate system from a cartesian coordinates
     *
     * @param coordinates the Cartesian coordinates of a point
     */
    private void getCoordinates(CartesianCoordinates coordinates) {
        double x = coordinates.getX();
        double y = coordinates.getY();

        r = Math.hypot(x, y);
        // Purposely flip the X and Y to get rotation on the X relative to Y
        theta = Math.atan2(y, x);
    }

    /**
     * Gets the distance from the origin for this point
     *
     * @return distance
     */
    public double getR() {
        return r;
    }

    /**
     * Gets the theta in radians for this point
     *
     * @return theta in radians
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Inverts the current point (reflect over y=x axis)
     *
     * @return this object after being reflected
     */
    public PolarCoordinates invert() {
        CartesianCoordinates temp = new CartesianCoordinates(this);
        temp.invert();
        getCoordinates(temp);

        return this;
    }

    /**
     * Gets the current theta in degrees
     *
     * @return theta in degrees
     */
    public double getThetaInDegrees() {
        return theta / Math.PI * 180;
    }
}
