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
package org.ftccommunity.ftcxtensible.autonomous.hardware;

import org.ftccommunity.ftcxtensible.internal.Alpha;

@Deprecated
public interface BasicSensor {
    /**
     * The Hardware Name for the sensor, as-is from the Robot Configuration
     *
     * @return the configuration HW Name for the motor
     */
    String getHWName();


    /**
     * Reads from the sensor
     *
     * @return a double representing one of the avialable inputs from the sensor
     */
    double read();

    /**
     * Writes an object to the sensor, implementation dependent
     */
    void write(Object mode);

    /**
     * Forces a update from the HW as presented by the FTC SDK
     */
    void readFromHW();

    /**
     * Flushes any pending writes to the hardware
     */
    void writeToHW();

    /**
     * Whether or not the sensor was created during run-time
     *
     * @return sensor runtime creation
     */
    boolean isNew();
}
