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

package org.ftccommunity.ftcxtensible.autonomous;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;

import org.ftccommunity.ftcxtensible.autonomous.hardware.BasicSensor;

/**
 * A Ir Sensor
 *
 * @author David Sargent
 * @since 0.0.5
 *
 */
@Deprecated
public class IrSensor implements BasicSensor {
    final HardwareMap hardwareMap = new HardwareMap();
    private IrSeekerSensor me;

    private String hw_name;
    private String common_name;
    private double angle;
    private boolean updated;
    private IrSeekerSensor.Mode temp_mode;
    private IrSeekerSensor.Mode mode;
    private double strength;
    private boolean signal;

    public IrSensor(final String motor_name) {
        hw_name = motor_name;
        common_name = motor_name;
        me = hardwareMap.irSeekerSensor.get(hw_name);
    }

    public IrSensor(final String hardware_name, final String name) {
        hw_name = hardware_name;
        common_name = name;
        me = hardwareMap.irSeekerSensor.get(hw_name);
    }

    public void writeToHW() {
        hardwareMap.irSeekerSensor.get(hw_name).setMode(temp_mode);
        mode = temp_mode;
    }

    public String getName() {
        return common_name;
    }

    public String getHWName() {
        return hw_name;
    }

    public boolean isNew() {
        return updated;
    }

    public void readFromHW() {

        mode = me.getMode();
        angle = me.getAngle();
        strength = me.getStrength();
        signal = me.signalDetected();
        updated = true;
    }

    public double read() {
        updated = false;
        return angle;
    }

    /**
     * Writes an object to the sensor, implementation dependent
     *
     * @param mode the mode of the IrSensor
     */
    @Override
    public void write(Object mode) {
        throw new IllegalStateException("Stub");
    }

    public IrSeekerSensor.Mode GetMode() {
        return mode;
    }

    public double GetStrength() {
        return strength;
    }

    public boolean HasSignal() {
        return signal;
    }

    public double GetAngle() {
        return read();
    }

    public void write(IrSeekerSensor.Mode new_mode) {
        temp_mode = new_mode;
    }

}
