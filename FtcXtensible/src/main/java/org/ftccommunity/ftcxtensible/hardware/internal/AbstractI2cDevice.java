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

package org.ftccommunity.ftcxtensible.hardware.internal;

import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cController;

import org.ftccommunity.ftcxtensible.hardware.I2cDevice;

import java.util.concurrent.locks.Lock;

public class AbstractI2cDevice implements I2cDevice {
    private I2cController i2cController = null;
    private int port = -1;

    public AbstractI2cDevice(I2cController controller, int port) {
        this.i2cController = controller;
        this.port = port;
    }

    @Override
    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        this.i2cController.enableI2cReadMode(this.port, i2cAddress, memAddress, length);
    }

    @Override
    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        this.i2cController.enableI2cWriteMode(this.port, i2cAddress, memAddress, length);
    }

    @Override
    public byte[] getCopyOfReadBuffer() {
        return this.i2cController.getCopyOfReadBuffer(this.port);
    }

    @Override
    public byte[] getCopyOfWriteBuffer() {
        return this.i2cController.getCopyOfWriteBuffer(this.port);
    }

    @Override
    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        this.i2cController.copyBufferIntoWriteBuffer(this.port, buffer);
    }

    @Override
    public void setI2cPortActionFlag() {
        this.i2cController.setI2cPortActionFlag(this.port);
    }

    @Override
    public boolean isI2cPortActionFlagSet() {
        return this.i2cController.isI2cPortActionFlagSet(this.port);
    }

    @Override
    public void readI2cCacheFromController() {
        this.i2cController.readI2cCacheFromController(this.port);
    }

    @Override
    public void writeI2cCacheToController() {
        this.i2cController.writeI2cCacheToController(this.port);
    }

    @Override
    public void writeI2cPortFlagOnlyToController() {
        this.i2cController.writeI2cPortFlagOnlyToController(this.port);
    }

    @Override
    public boolean isI2cPortInReadMode() {
        return this.i2cController.isI2cPortInReadMode(this.port);
    }

    @Override
    public boolean isI2cPortInWriteMode() {
        return this.i2cController.isI2cPortInWriteMode(this.port);
    }

    @Override
    public boolean isI2cPortReady() {
        return this.i2cController.isI2cPortReady(this.port);
    }

    @Override
    public Lock getI2cReadCacheLock() {
        return this.i2cController.getI2cReadCacheLock(this.port);
    }

    @Override
    public Lock getI2cWriteCacheLock() {
        return this.i2cController.getI2cWriteCacheLock(this.port);
    }

    @Override
    public byte[] getI2cReadCache() {
        return this.i2cController.getI2cReadCache(this.port);
    }

    @Override
    public byte[] getI2cWriteCache() {
        return this.i2cController.getI2cWriteCache(this.port);
    }

    @Override
    public void registerForI2cPortReadyCallback(I2cController.I2cPortReadyCallback callback) {
        this.i2cController.registerForI2cPortReadyCallback(callback, this.port);
    }

    @Override
    public void deregisterForPortReadyCallback() {
        this.i2cController.deregisterForPortReadyCallback(this.port);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void readI2cCacheFromModule() {
        this.readI2cCacheFromController();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void writeI2cCacheToModule() {
        this.writeI2cCacheToController();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void writeI2cPortFlagOnlyToModule() {
        this.writeI2cPortFlagOnlyToController();
    }

    public String getDeviceName() {
        return "I2cDevice";
    }

    public String getConnectionInfo() {
        return this.i2cController.getConnectionInfo() + "; port " + this.port;
    }

    public I2cController getI2cController() {
        return i2cController;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {}
}
