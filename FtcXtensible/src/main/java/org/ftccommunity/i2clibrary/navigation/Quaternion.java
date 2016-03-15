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

package org.ftccommunity.i2clibrary.navigation;

import org.ftccommunity.i2clibrary.interfaces.II2cDeviceClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A Quaternion can indicate an orientation in three-space without the trouble of possible
 * gimbal-lock.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Quaternion">https://en.wikipedia.org/wiki/Quaternion</a>
 * @see <a href="https://en.wikipedia.org/wiki/Gimbal_lock">https://en.wikipedia.org/wiki/Gimbal_lock</a>
 * @see <a href="https://www.youtube.com/watch?v=zc8b2Jo7mno">https://www.youtube.com/watch?v=zc8b2Jo7mno</a>
 * @see <a href="https://www.youtube.com/watch?v=mHVwd8gYLnI">https://www.youtube.com/watch?v=mHVwd8gYLnI</a>
 */
public class Quaternion {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    public final double w;
    public final double x;
    public final double y;
    public final double z;

    /**
     * the time on the System.nanoTime() clock at which the data was acquired. If no timestamp is
     * associated with this particular set of data, this value is zero
     */
    public final long nanoTime;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public Quaternion() {
        this(1, 0, 0, 0, 0);
    }

    public Quaternion(double w, double x, double y, double z, long nanoTime) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.nanoTime = nanoTime;
    }

    public Quaternion(II2cDeviceClient.TimestampedData ts, double scale) {
        ByteBuffer buffer = ByteBuffer.wrap(ts.data).order(ByteOrder.LITTLE_ENDIAN);
        this.w = buffer.getShort() / scale;
        this.x = buffer.getShort() / scale;
        this.y = buffer.getShort() / scale;
        this.z = buffer.getShort() / scale;
        this.nanoTime = ts.nanoTime;
    }

    //----------------------------------------------------------------------------------------------
    // Operations
    //----------------------------------------------------------------------------------------------

    public double magnitude() {
        return Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternion normalized() {
        double mag = this.magnitude();
        return new Quaternion(
                w / mag,
                x / mag,
                y / mag,
                z / mag,
                this.nanoTime);
    }

    public Quaternion congugate() {
        return new Quaternion(w, -x, -y, -z, this.nanoTime);
    }
}
