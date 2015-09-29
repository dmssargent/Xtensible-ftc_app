/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
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

import java.util.HashMap;

/**
 *
 */
public class ImmutableHardwareMap {
    private final HashMap<String, ImmutableMap> deviceMap;

    public ImmutableHardwareMap(ExtensibleHardwareMap hardwareMap) {
        deviceMap = new HashMap<>();
        deviceMap.put(DcMotorController.class.getSimpleName(), hardwareMap.getDcMotorControllers());
        deviceMap.put(DcMotor.class.getSimpleName(), hardwareMap.getDcMotors());
        deviceMap.put(ServoController.class.getSimpleName(), hardwareMap.getServoControllers());
        deviceMap.put(Servo.class.getSimpleName(), hardwareMap.getServos());
        deviceMap.put(LegacyModule.class.getSimpleName(), hardwareMap.getLegacyModules());
        deviceMap.put(DeviceInterfaceModule.class.getSimpleName(),
                hardwareMap.getDeviceInterfaceModules());

        deviceMap.put(AnalogInput.class.getSimpleName(), hardwareMap.getAnalogInputs());
        deviceMap.put(DigitalChannel.class.getSimpleName(), hardwareMap.getDigitalChannels());
        deviceMap.put(OpticalDistanceSensor.class.getSimpleName(), hardwareMap.getOpticalDistanceSensors());
        deviceMap.put(TouchSensor.class.getSimpleName(), hardwareMap.getTouchSensors());
        deviceMap.put(PWMOutput.class.getSimpleName(), hardwareMap.getPwmOutputs());
        deviceMap.put(I2cDevice.class.getSimpleName(), hardwareMap.getI2cDevices());
        deviceMap.put(AnalogOutput.class.getSimpleName(), hardwareMap.getAnalogOutputs());
        deviceMap.put(AnalogOutput.class.getSimpleName(), hardwareMap.getAnalogInputs());
        deviceMap.put(AccelerationSensor.class.getSimpleName(), hardwareMap.getAccelerationSensors());
        deviceMap.put(CompassSensor.class.getSimpleName(), hardwareMap.getCompassSensors());
        deviceMap.put(GyroSensor.class.getSimpleName(), hardwareMap.getGyroSensors());
        deviceMap.put(IrSeekerSensor.class.getSimpleName(), hardwareMap.getIrSeekerSensors());
        deviceMap.put(LightSensor.class.getSimpleName(), hardwareMap.getLightSensors());
        deviceMap.put(UltrasonicSensor.class.getSimpleName(), hardwareMap.getUltrasonicSensors());
        deviceMap.put(VoltageSensor.class.getSimpleName(), hardwareMap.getVoltageSensors());
    }

    public HashMap<String, ImmutableMap> getUnderlyingMap() {
        return deviceMap;
    }
}
