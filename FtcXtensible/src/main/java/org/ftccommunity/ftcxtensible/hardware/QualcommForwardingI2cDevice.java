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

public abstract class QualcommForwardingI2cDevice extends ForwardingHardwareDevice<com.qualcomm.robotcore.hardware.I2cDevice> implements I2cDevice {
    @Override
    protected abstract com.qualcomm.robotcore.hardware.I2cDevice delegate();

    @Override
    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        delegate().enableI2cReadMode(i2cAddress, memAddress, length);
    }

    @Override
    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        delegate().enableI2cReadMode(i2cAddress, memAddress, length);
    }

    @Override
    public byte[] getCopyOfReadBuffer() {
        return delegate().getCopyOfReadBuffer();
    }

    @Override
    public byte[] getCopyOfWriteBuffer() {
        return delegate().getCopyOfWriteBuffer();
    }

    @Override
    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        delegate().copyBufferIntoWriteBuffer(buffer);
    }

    @Override
    public void setI2cPortActionFlag() {
        delegate().setI2cPortActionFlag();
    }

    @Override
    public boolean isI2cPortActionFlagSet() {
        return delegate().isI2cPortActionFlagSet();
    }

    @Override
    public void readI2cCacheFromController() {
        delegate().readI2cCacheFromController();
    }

    @Override
    public void writeI2cCacheToController() {
        delegate().writeI2cCacheToController();
    }

    @Override
    public void writeI2cPortFlagOnlyToController() {
        delegate().writeI2cPortFlagOnlyToController();
    }

    @Override
    public boolean isI2cPortInReadMode() {
        return delegate().isI2cPortInReadMode();
    }

    @Override
    public boolean isI2cPortInWriteMode() {
        return delegate().isI2cPortInWriteMode();
    }

    @Override
    public boolean isI2cPortReady() {
        return delegate().isI2cPortReady();
    }

    @Override
    public Lock getI2cReadCacheLock() {
        return delegate().getI2cReadCacheLock();
    }

    @Override
    public Lock getI2cWriteCacheLock() {
        return delegate().getI2cWriteCacheLock();
    }

    @Override
    public byte[] getI2cReadCache() {
        return delegate().getI2cReadCache();
    }

    @Override
    public byte[] getI2cWriteCache() {
        return delegate().getI2cWriteCache();
    }

    @Override
    public void registerForI2cPortReadyCallback(I2cController.I2cPortReadyCallback callback) {
        delegate().registerForI2cPortReadyCallback(callback);
    }

    @Override
    public void deregisterForPortReadyCallback() {
        delegate().deregisterForPortReadyCallback();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void readI2cCacheFromModule() {
        //noinspection deprecation
        delegate().readI2cCacheFromModule();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void writeI2cCacheToModule() {
        //noinspection deprecation
        delegate().writeI2cCacheToModule();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void writeI2cPortFlagOnlyToModule() {
        //noinspection deprecation
        delegate().writeI2cPortFlagOnlyToModule();
    }
}
