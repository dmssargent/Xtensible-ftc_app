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
 * AngularVelocity represents a rotation rate in three-space. Units are as specified in sensor
 * initialization, either radians/second or degrees/second.
 */
public class AngularVelocity {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    /**
     * the rotational rate about the X axis
     */
    public final double rateX;
    /**
     * the rotational rate about the Y axis
     */
    public final double rateY;
    /**
     * the rotational rate about the Z axis
     */
    public final double rateZ;

    /**
     * the time on the System.nanoTime() clock at which the data was acquired. If no timestamp is
     * associated with this particular set of data, this value is zero
     */
    public final long nanoTime;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public AngularVelocity() {
        this(0, 0, 0, 0);
    }

    public AngularVelocity(double rateX, double rateY, double rateZ, long nanoTime) {
        this.rateX = rateX;
        this.rateY = rateY;
        this.rateZ = rateZ;
        this.nanoTime = nanoTime;
    }

    public AngularVelocity(II2cDeviceClient.TimestampedData ts, double scale) {
        ByteBuffer buffer = ByteBuffer.wrap(ts.data).order(ByteOrder.LITTLE_ENDIAN);
        this.rateX = buffer.getShort() / scale;
        this.rateY = buffer.getShort() / scale;
        this.rateZ = buffer.getShort() / scale;
        this.nanoTime = ts.nanoTime;
    }
}
