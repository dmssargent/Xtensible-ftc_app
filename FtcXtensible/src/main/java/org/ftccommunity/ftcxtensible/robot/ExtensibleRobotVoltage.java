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

package org.ftccommunity.ftcxtensible.robot;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.ftccommunity.ftcxtensible.math.Numericalize;
import org.ftccommunity.ftcxtensible.math.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtensibleRobotVoltage implements VoltageSensor {
    private final Numericalize<VoltageSensor> numericalizer;
    private LinkedList<VoltageSensor> sensors;

    public ExtensibleRobotVoltage(@NotNull List<VoltageSensor> voltageSensors) {
        sensors = new LinkedList<>(checkNotNull(voltageSensors));
        numericalizer = new Numericalize<VoltageSensor>() {
            @Override
            public double toNumber(VoltageSensor object) {
                return object.getVoltage();
            }
        };
    }

    public void addVoltageSensor(@NotNull VoltageSensor sensor) {
        sensors.add(sensor);
    }

    public List<VoltageSensor> sensors() {
        return ImmutableList.copyOf(sensors);
    }

    @Override
    public double getVoltage() {
        return Stat.mean(sensors, numericalizer);
    }

    @Override
    public String getDeviceName() {
        return "Xtensible Robot Voltage Sensor";
    }

    @Override
    public String getConnectionInfo() {
        return "Emulated";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (VoltageSensor sensor : sensors) {
            sensor.close();
        }

        sensors = null;
    }
}
