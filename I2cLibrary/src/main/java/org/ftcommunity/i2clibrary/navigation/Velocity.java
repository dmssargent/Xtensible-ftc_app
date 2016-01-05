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
 * Velocity represents a directed velocity in three-space. Units are as the same as for
 * Acceleration, but integrated for time.
 */
public class Velocity {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    /**
     * the velocity in the X direction
     */
    public final double velocX;
    /**
     * the velocity in the Y direction
     */
    public final double velocY;
    /**
     * the velocity in the Z direction
     */
    public final double velocZ;

    /**
     * the time on the System.nanoTime() clock at which the data was acquired. If no timestamp is
     * associated with this particular set of data, this value is zero
     */
    public final long nanoTime;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public Velocity() {
        this(0, 0, 0, 0);
    }

    public Velocity(double velocX, double velocY, double velocZ, long nanoTime) {
        this.velocX = velocX;
        this.velocY = velocY;
        this.velocZ = velocZ;
        this.nanoTime = nanoTime;
    }

    public Velocity(II2cDeviceClient.TimestampedData ts, double scale) {
        ByteBuffer buffer = ByteBuffer.wrap(ts.data).order(ByteOrder.LITTLE_ENDIAN);
        this.velocX = buffer.getShort() / scale;
        this.velocY = buffer.getShort() / scale;
        this.velocZ = buffer.getShort() / scale;
        this.nanoTime = ts.nanoTime;
    }

}
