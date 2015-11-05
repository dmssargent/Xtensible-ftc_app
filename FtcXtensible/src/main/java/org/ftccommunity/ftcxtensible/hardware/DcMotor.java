/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.hardware;

import com.google.common.base.Throwables;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.abstraction.hardware.HardwareDevice;
import org.ftccommunity.ftcxtensible.abstraction.hardware.Mockable;
import org.ftccommunity.ftcxtensible.robot.connection.ConnectionInfo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * A simpler DcMotor implementation designed to be fast-failing in case of error
 */
public class DcMotor extends com.qualcomm.robotcore.hardware.DcMotor implements HardwareDevice, Mockable<DcMotor> {
    private final Lock motorLock;
    private volatile double speed;
    private String name;
    private ConnectionInfo info;
    private boolean isMock;

    public DcMotor(DcMotorController controller, int portNumber) {
        this(controller, portNumber, Direction.FORWARD);
    }

    public DcMotor(DcMotorController controller, int portNumber, Direction direction) {
        super(controller, portNumber, direction);

        motorLock = new ReentrantLock();
        info = new ConnectionInfo(ConnectionInfo.ConnectionType.USB,
                ConnectionInfo.ConnectionState.GOOD);

        isMock = false;
    }

    @Override
    public void setPower(double speed) throws RuntimeException {
        checkArgument(speed >= -1, "Speed must be greater than -1");
        checkArgument(speed <= 1, "Speed must be less than 1");
        checkState(
                controller.getMotorControllerDeviceMode() != DcMotorController.DeviceMode.READ_ONLY,
                "Motor Controller is in READ ONLY mode, not assigning speed");

        try {
            if (motorLock.tryLock(10, TimeUnit.MILLISECONDS)) {
                super.setPower(speed);
                this.speed = speed;
            } else {
                throw new RuntimeException("Cannot lock the motor to set the speed");
            }
        } catch (InterruptedException ex) {
            Throwables.propagate(ex);
        }
    }

    public void floatMotor() {
        setPowerFloat();
    }

    public void brake() {
        setPower(0d);
    }

    public double getLastAssignedSpeed() {
        return speed;
    }

    @Override
    public ConnectionInfo getConnection() {
        return info;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(CharSequence cs) {
        name = cs.toString();
    }

    @Override
    public int getPdmPort() {
        return portNumber;
    }

    @Override
    public boolean isMockable() {
        return true;
    }

    @Override
    public boolean isMockDevice() {
        return isMock;
    }

    @Override
    public DcMotor mockup() {
        throw new IllegalArgumentException("Stub method");
    }
}
