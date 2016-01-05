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

import org.ftcommunity.i2clibrary.interfaces.II2cDeviceClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Instances of EulerAngles represent a direction in three-dimensional space by way of rotations.
 * Units are as specified in sensor initiation. Angles are in rotation order (heading, then roll,
 * then pitch) and are right-handed about their respective axes.
 */
public class EulerAngles {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    /**
     * the rotation about the Z axis
     */
    public final double heading;
    /**
     * the rotation about the Y axis
     */
    public final double roll;
    /**
     * the rotation about the X axix
     */
    public final double pitch;

    /**
     * the time on the System.nanoTime() clock at which the data was acquired. If no timestamp is
     * associated with this particular set of data, this value is zero
     */
    public final long nanoTime;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public EulerAngles() {
        this(0, 0, 0, 0);
    }

    public EulerAngles(double heading, double roll, double pitch, long nanoTime) {
        this.heading = heading;
        this.roll = roll;
        this.pitch = pitch;
        this.nanoTime = nanoTime;
    }

    public EulerAngles(II2cDeviceClient.TimestampedData ts, double scale) {
        ByteBuffer buffer = ByteBuffer.wrap(ts.data).order(ByteOrder.LITTLE_ENDIAN);
        this.heading = buffer.getShort() / scale;
        this.roll = buffer.getShort() / scale;
        this.pitch = buffer.getShort() / scale;
        this.nanoTime = ts.nanoTime;
    }
}
