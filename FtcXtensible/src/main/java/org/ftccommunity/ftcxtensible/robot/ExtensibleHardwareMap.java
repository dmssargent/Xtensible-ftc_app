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

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
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

import org.ftccommunity.ftcxtensible.collections.DeviceMap;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.util.I2cFactory;
import org.ftcommunity.i2clibrary.Wire;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The new version of the hardware map that provides a safer use for the hardware map.
 * This prevents client code from unintentionally modifying the devices avialable for use
 *
 * @author David Sargent
 * @since 0.1
 */
@Alpha
public class ExtensibleHardwareMap {
    private HardwareMap basicMap;

    private DeviceMultiMap fullMap;
    private LinkedList<DeviceMap<String, ? extends HardwareDevice>> allMaps;

    /**
     * Builds the base ExtensibleHardwareMap; need to complete setup afterwards, specifically calling
     * {@link ExtensibleHardwareMap#createDeviceMaps()}
     */
    private ExtensibleHardwareMap() {
        fullMap = new DeviceMultiMap();
        allMaps = new LinkedList<>();
    }

    /**
     * Builds a new {@code ExtensibleHardwareMap} for use
     *
     * @param hwMap the HardwareMap to base off of
     */
    public ExtensibleHardwareMap(@NotNull HardwareMap hwMap) {
        this();
        basicMap = checkNotNull(hwMap);
        createDeviceMaps();
    }

    /**
     * Rebuilds the Extensible HardwareMap from scacch based on a given {@link HardwareMap}. This modifies
     * the reference to the maps within inside this HardwareMap, but not the reference to the HardwareMap
     * itself.
     *
     * @param hwMap a valid non-null {@code HardwareMap} that contains what this needs to build on
     */
    public void rebuild(@NotNull HardwareMap hwMap) {
        basicMap = checkNotNull(hwMap);
        createDeviceMaps();
    }

   /* public static boolean supportsMocking(Object object) {
        return object.getClass().isAssignableFrom(Mockable.class);

    }*/

    /**
     * Move the propriety {@link HardwareMap.DeviceMapping} to our
     * {@link DeviceMap} for our
     * internal use
     */
    private void createDeviceMaps() {
        fullMap.insert(DcMotorController.class, DeviceMap.buildFromDeviceMapping(basicMap.dcMotorController));
        fullMap.insert(DcMotor.class, DeviceMap.buildFromDeviceMapping(basicMap.dcMotor));
        fullMap.insert(ServoController.class, DeviceMap.buildFromDeviceMapping(basicMap.servoController));
        fullMap.insert(Servo.class, DeviceMap.buildFromDeviceMapping(basicMap.servo));
        fullMap.insert(LegacyModule.class, DeviceMap.buildFromDeviceMapping(basicMap.legacyModule));
        fullMap.insert(DeviceInterfaceModule.class, DeviceMap.buildFromDeviceMapping(basicMap.deviceInterfaceModule));
        fullMap.insert(AnalogInput.class, DeviceMap.buildFromDeviceMapping(basicMap.analogInput));
        fullMap.insert(DigitalChannel.class, DeviceMap.buildFromDeviceMapping(basicMap.digitalChannel));
        fullMap.insert(OpticalDistanceSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.opticalDistanceSensor));
        fullMap.insert(TouchSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.touchSensor));
        fullMap.insert(PWMOutput.class, DeviceMap.buildFromDeviceMapping(basicMap.pwmOutput));
        fullMap.insert(I2cDevice.class, DeviceMap.buildFromDeviceMapping(basicMap.i2cDevice));
        fullMap.insert(AnalogOutput.class, DeviceMap.buildFromDeviceMapping(basicMap.analogOutput));
        fullMap.insert(AccelerationSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.accelerationSensor));
        fullMap.insert(CompassSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.compassSensor));
        fullMap.insert(GyroSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.gyroSensor));
        fullMap.insert(IrSeekerSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.irSeekerSensor));
        fullMap.insert(LightSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.lightSensor));
        fullMap.insert(UltrasonicSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.ultrasonicSensor));
        fullMap.insert(VoltageSensor.class, DeviceMap.buildFromDeviceMapping(basicMap.voltageSensor));

        for (Map.Entry<Class, DeviceMap<String, ? extends HardwareDevice>> map :
                fullMap.entrySet()) {
            allMaps.add(map.getValue());
        }
    }

    public TypedHardwareDevice get(String name) {
        for (Map.Entry<Class, DeviceMap<String, ? extends HardwareDevice>> map : fullMap.entrySet()) {
            DeviceMap deviceMap = map.getValue();
            if (deviceMap.containsKey(name)) {
                return new TypedHardwareDevice<>(map.getKey(), deviceMap.get(name), name);
            }
        }

        return null;
    }

    public DeviceMap<String, DcMotorController> dcMotorControllers() {
        return fullMap.fetch(DcMotorController.class);
    }

    public DeviceMap<String, DcMotor> dcMotors() {
        return fullMap.fetch(DcMotor.class);
    }

    public DeviceMap<String, ServoController> servoControllers() {
        return fullMap.fetch(ServoController.class);
    }

    public DeviceMap<String, Servo> servos() {
        return fullMap.fetch(Servo.class);
    }

    public DeviceMap<String, LegacyModule> legacyModules() {
        return fullMap.fetch(LegacyModule.class);
    }

    public DeviceMap<String, DeviceInterfaceModule> deviceInterfaceModules() {
        return fullMap.fetch(DeviceInterfaceModule.class);
    }

    public DeviceMap<String, AnalogInput> analogInputs() {
        return fullMap.fetch(AnalogInput.class);
    }

    public DeviceMap<String, DigitalChannel> digitalChannels() {
        return fullMap.fetch(DigitalChannel.class);
    }

    public DeviceMap<String, OpticalDistanceSensor> opticalDistanceSensors() {
        return fullMap.fetch(OpticalDistanceSensor.class);
    }

    public DeviceMap<String, TouchSensor> touchSensors() {
        return fullMap.fetch(TouchSensor.class);
    }

    public DeviceMap<String, PWMOutput> pwmOutputs() {
        return fullMap.fetch(PWMOutput.class);
    }

    public DeviceMap<String, I2cDevice> i2cDevices() {
        return fullMap.fetch(I2cDevice.class);
    }

    public Wire wire(String name, byte address) {
        I2cDevice i2cDevice = i2cDevices().get(name);
        if (i2cDevice == null) {
            throw new IllegalArgumentException("The device " + name + " is not found.");
        }
        return I2cFactory.createWire(i2cDevice, address);
    }

    public DeviceMap<String, AnalogOutput> analogOutputs() {
        return fullMap.fetch(AnalogOutput.class);
    }

    public DeviceMap<String, AccelerationSensor> accelerationSensors() {
        return fullMap.fetch(AccelerationSensor.class);
    }

    public DeviceMap<String, CompassSensor> compassSensors() {
        return fullMap.fetch(CompassSensor.class);
    }

    public DeviceMap<String, GyroSensor> gyroSensors() {
        return fullMap.fetch(GyroSensor.class);
    }

    public DeviceMap<String, IrSeekerSensor> irSeekerSensors() {
        return fullMap.fetch(IrSeekerSensor.class);
    }

    public DeviceMap<String, LightSensor> lightSensors() {
        return fullMap.fetch(LightSensor.class);
    }

    public DeviceMap<String, UltrasonicSensor> ultrasonicSensors() {
        return fullMap.fetch(UltrasonicSensor.class);
    }

    public DeviceMap<String, VoltageSensor> voltageSensors() {
        return fullMap.fetch(VoltageSensor.class);
    }

    public class DeviceMultiMap extends HashMap<Class, DeviceMap<String, ? extends HardwareDevice>> {
        public <T extends HardwareDevice> DeviceMap<String, T> fetch(Class<T> object) {
            if (!super.containsKey(object)) {
                throw new IllegalArgumentException(String.format("Map doesn't contain %s", object.getSimpleName()));
            }

            return (DeviceMap<String, T>) super.get(object);
        }

        public <T extends HardwareDevice> void insert(Class<T> klazz, DeviceMap<String, T> map) {
            super.put(klazz, map);
        }
    }

    public static class TypedHardwareDevice<T extends HardwareDevice> {
        private Class<T> klazz;
        private T hardwareDevice;
        private String name;

        public TypedHardwareDevice(Class<T> klazz, T hardwareDevice, String name) {
            this.klazz = klazz;
            this.hardwareDevice = hardwareDevice;
            this.name = name;
        }

        public Class<T> type() {
            return klazz;
        }

        public T hardwareDevice() {
            return hardwareDevice;
        }

        public String name() {
            return name;
        }
    }
}
