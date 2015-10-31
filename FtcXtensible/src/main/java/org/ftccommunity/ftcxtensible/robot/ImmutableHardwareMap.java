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

package org.ftccommunity.ftcxtensible.robot;

import com.google.common.collect.ImmutableMap;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;

import java.util.HashMap;


@NotDocumentedWell
@Alpha
public class ImmutableHardwareMap {
    private final HashMap<String, ImmutableMap> deviceMap;

    public ImmutableHardwareMap(ExtensibleHardwareMap hardwareMap) {
        deviceMap = new HashMap<>();
        deviceMap.put(DcMotorController.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.dcMotorControllers()));
        deviceMap.put(DcMotor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.dcMotors()));
        deviceMap.put(ServoController.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.servoControllers()));
        deviceMap.put(Servo.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.servos()));
        deviceMap.put(LegacyModule.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.legacyModules()));
        deviceMap.put(DeviceInterfaceModule.class.getSimpleName(),
                ImmutableMap.copyOf(hardwareMap.deviceInterfaceModules()));

        deviceMap.put(AnalogInput.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.analogInputs()));
        deviceMap.put(DigitalChannel.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.digitalChannels()));
        deviceMap.put(OpticalDistanceSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.opticalDistanceSensors()));
        deviceMap.put(TouchSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.touchSensors()));
        deviceMap.put(PWMOutput.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.pwmOutputs()));
        deviceMap.put(I2cDevice.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.i2cDevices()));
        deviceMap.put(AnalogOutput.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.analogOutputs()));
        deviceMap.put(AnalogOutput.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.analogInputs()));
        deviceMap.put(AccelerationSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.accelerationSensors()));
        deviceMap.put(CompassSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.compassSensors()));
        deviceMap.put(GyroSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.gyroSensors()));
        deviceMap.put(IrSeekerSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.irSeekerSensors()));
        deviceMap.put(LightSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.lightSensors()));
        deviceMap.put(UltrasonicSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.ultrasonicSensors()));
        deviceMap.put(VoltageSensor.class.getSimpleName(), ImmutableMap.copyOf(hardwareMap.voltageSensors()));
    }

    public HashMap<String, ImmutableMap> getUnderlyingMap() {
        return deviceMap;
    }
}
