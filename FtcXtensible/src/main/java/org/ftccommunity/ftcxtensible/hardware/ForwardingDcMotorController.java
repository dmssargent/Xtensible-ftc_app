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

import com.qualcomm.robotcore.hardware.DcMotorController;

public abstract class ForwardingDcMotorController extends ForwardingHardwareDevice<DcMotorController> implements DcMotorController {
    @Override
    public DeviceMode getMotorControllerDeviceMode() {
        return delegate().getMotorControllerDeviceMode();
    }

    @Override
    public void setMotorControllerDeviceMode(DeviceMode deviceMode) {
        delegate().setMotorControllerDeviceMode(deviceMode);
    }

    @Override
    public void setMotorChannelMode(int port, RunMode runMode) {
        delegate().setMotorChannelMode(port, runMode);
    }

    @Override
    public RunMode getMotorChannelMode(int port) {
        return delegate().getMotorChannelMode(port);
    }

    @Override
    public void setMotorPower(int port, double power) {
        delegate().setMotorPower(port, power);
    }

    @Override
    public double getMotorPower(int port) {
        return delegate().getMotorPower(port);
    }

    @Override
    public boolean isBusy(int port) {
        return delegate().isBusy(port);
    }

    @Override
    public void setMotorPowerFloat(int port) {
        delegate().setMotorPowerFloat(port);
    }

    @Override
    public boolean getMotorPowerFloat(int port) {
        return delegate().getMotorPowerFloat(port);
    }

    @Override
    public void setMotorTargetPosition(int port, int position) {
        delegate().setMotorTargetPosition(port, position);
    }

    @Override
    public int getMotorTargetPosition(int port) {
        return delegate().getMotorTargetPosition(port);
    }

    @Override
    public int getMotorCurrentPosition(int port) {
        return delegate().getMotorCurrentPosition(port);
    }

    @Override
    protected abstract DcMotorController delegate();
}
