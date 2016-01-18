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

package org.ftccommunity.ftcxtensible.hardware;

import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cController;

import java.util.concurrent.locks.Lock;

public interface I2cDevice extends HardwareDevice {
    void enableI2cReadMode(int i2cAddress, int memAddress, int length);

    void enableI2cWriteMode(int i2cAddress, int memAddress, int length);

    byte[] getCopyOfReadBuffer();

    byte[] getCopyOfWriteBuffer();

    void copyBufferIntoWriteBuffer(byte[] buffer);

    void setI2cPortActionFlag();

    boolean isI2cPortActionFlagSet();

    void readI2cCacheFromController();

    void writeI2cCacheToController();

    void writeI2cPortFlagOnlyToController();

    boolean isI2cPortInReadMode();

    boolean isI2cPortInWriteMode();

    boolean isI2cPortReady();

    Lock getI2cReadCacheLock();

    Lock getI2cWriteCacheLock();

    byte[] getI2cReadCache();

    byte[] getI2cWriteCache();

    void registerForI2cPortReadyCallback(I2cController.I2cPortReadyCallback callback);

    void deregisterForPortReadyCallback();


    /** @deprecated */
    @Deprecated
    void readI2cCacheFromModule();

    /** @deprecated */
    @Deprecated
    void writeI2cCacheToModule();

    /** @deprecated */
    @Deprecated
    void writeI2cPortFlagOnlyToModule();
}
