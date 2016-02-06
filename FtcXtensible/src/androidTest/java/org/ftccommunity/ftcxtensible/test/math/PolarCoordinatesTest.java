///*
// * Copyright © 2016 David Sargent
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// * and associated documentation files (the "Software"), to deal in the Software without restriction,
// * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
// * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
// * the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or
// * substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
// * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */
//
//package org.ftccommunity.ftcxtensible.test.math;
//
//
//import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
//import org.ftccommunity.ftcxtensible.math.PolarCoordinates;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class PolarCoordinatesTest {
//    @Test
//    public void testCartesianCoordinatesConstructor() {
//        PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(-1, 0));
//        assertEquals(Math.PI, coordinates.getTheta(), 0.001);
//        assertEquals(180, coordinates.getThetaInDegrees(), 0.001);
//    }
//
//    @Test
//    public void testInvert() {
//        PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(453, 653));
//        double r = coordinates.getR();
//        double theta = coordinates.getTheta();
//        coordinates = coordinates.invert();
//        assertEquals(r, coordinates.getR(), 0.00001);
//        assertEquals(theta, 2 * Math.PI - coordinates.getTheta(), 0.001);
//    }
//
//    @Test
//    public void testGetThetaInDegrees() {
//        PolarCoordinates coordinates = new PolarCoordinates(45, Math.PI);
//        assertEquals(180, coordinates.getThetaInDegrees(), 0.0001);
//
//        coordinates = new PolarCoordinates(342, 2 * Math.PI);
//        assertEquals(360, coordinates.getThetaInDegrees(), 0.0001);
//
//        coordinates = new PolarCoordinates(343, 3 * Math.PI);
//        assertEquals(180, coordinates.getThetaInDegrees(), 0.0001);
//
//        coordinates = new PolarCoordinates(22, -Math.PI);
//        assertEquals(180, coordinates.getThetaInDegrees(), 0.0001);
//    }
//}