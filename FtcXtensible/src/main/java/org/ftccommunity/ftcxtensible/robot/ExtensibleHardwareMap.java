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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.reflect.TypeToken;
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

import org.ftccommunity.ftcxtensible.abstraction.hardware.Mockable;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private LinkedHashMultimap<String, HardwareDevice> fullMap;

    // Conversions to our DeviceMap
    private DeviceMap<String, DcMotorController> dcMotorControllers;
    private DeviceMap<String, DcMotor> dcMotors;
    private DeviceMap<String, ServoController> servoControllers;
    private DeviceMap<String, Servo> servos;
    private DeviceMap<String, LegacyModule> legacyModules;
    private DeviceMap<String, DeviceInterfaceModule> deviceInterfaceModules;
    private DeviceMap<String, AnalogInput> analogInputs;
    private DeviceMap<String, DigitalChannel> digitalChannels;
    private DeviceMap<String, OpticalDistanceSensor> opticalDistanceSensors;
    private DeviceMap<String, TouchSensor> touchSensors;
    private DeviceMap<String, PWMOutput> pwmOutputs;
    private DeviceMap<String, I2cDevice> i2cDevices;
    private DeviceMap<String, AnalogOutput> analogOutputs;
    private DeviceMap<String, AccelerationSensor> accelerationSensors;
    private DeviceMap<String, CompassSensor> compassSensors;
    private DeviceMap<String, GyroSensor> gyroSensors;
    private DeviceMap<String, IrSeekerSensor> irSeekerSensors;
    private DeviceMap<String, LightSensor> lightSensors;
    private DeviceMap<String, UltrasonicSensor> ultrasonicSensors;
    private DeviceMap<String, VoltageSensor> voltageSensors;

    //private LinkedHashMultimap<String, DeviceMap> cacheMap;

    /**
     * Builds the base ExtensibleHardwareMap; need to complete setup afterwards, specifically calling
     * {@link ExtensibleHardwareMap#createDeviceMaps()}
     */
    private ExtensibleHardwareMap() {
        // createDeviceMaps();
    }

    /**
     * Builds a new {@code ExtensibleHardwareMap} for use
     *
     * @param hwMap the HardwareMap to base off of
     */
    public ExtensibleHardwareMap(@NotNull HardwareMap hwMap) {
        this();
        basicMap = hwMap;

        createDeviceMaps();
    }

    public static boolean supportsMocking(Object object) {
        return object.getClass().isAssignableFrom(Mockable.class);

    }

    /**
     * Move the propriety {@link HardwareMap.DeviceMapping} to our
     * {@link DeviceMap} for our
     * internal use
     */
    private void createDeviceMaps() {
        dcMotorControllers = new DeviceMap<>(basicMap.dcMotorController);
        dcMotors = new DeviceMap<>(basicMap.dcMotor);
        servoControllers = new DeviceMap<>(basicMap.servoController);
        servos = new DeviceMap<>(basicMap.servo);
        legacyModules = new DeviceMap<>(basicMap.legacyModule);
        deviceInterfaceModules = new DeviceMap<>(basicMap.deviceInterfaceModule);
        analogInputs = new DeviceMap<>(basicMap.analogInput);
        digitalChannels = new DeviceMap<>(basicMap.digitalChannel);
        opticalDistanceSensors = new DeviceMap<>(basicMap.opticalDistanceSensor);
        touchSensors = new DeviceMap<>(basicMap.touchSensor);
        pwmOutputs = new DeviceMap<>(basicMap.pwmOutput);
        i2cDevices = new DeviceMap<>(basicMap.i2cDevice);
        analogOutputs = new DeviceMap<>(basicMap.analogOutput);
        accelerationSensors = new DeviceMap<>(basicMap.accelerationSensor);
        compassSensors = new DeviceMap<>(basicMap.compassSensor);
        gyroSensors = new DeviceMap<>(basicMap.gyroSensor);
        irSeekerSensors = new DeviceMap<>(basicMap.irSeekerSensor);
        lightSensors = new DeviceMap<>(basicMap.lightSensor);
        ultrasonicSensors = new DeviceMap<>(basicMap.ultrasonicSensor);
        voltageSensors = new DeviceMap<>(basicMap.voltageSensor);

        fullMap = LinkedHashMultimap.create();
        for (Map.Entry device : dcMotorControllers.entrySet()) {
            fullMap.entries().add(device);
        }
    }

    public HardwareDevice get(String name) {
        if (dcMotorControllers.containsKey(name)) {
            return dcMotorControllers.get(name);
        } else if (dcMotors.containsKey(name)) {
            return dcMotors.get(name);
        }

        return null;
    }

    public DeviceMap<String, DcMotorController> dcMotorControllers() {
        return dcMotorControllers;
    }

    public DeviceMap<String, DcMotor> dcMotors() {
        return dcMotors;
    }

    public DeviceMap<String, ServoController> servoControllers() {
        return servoControllers;
    }

    public DeviceMap<String, Servo> servos() {
        return servos;
    }

    public DeviceMap<String, LegacyModule> legacyModules() {
        return legacyModules;
    }

    public DeviceMap<String, DeviceInterfaceModule> deviceInterfaceModules() {
        return deviceInterfaceModules;
    }

    public DeviceMap<String, AnalogInput> analogInputs() {
        return analogInputs;
    }

    public DeviceMap<String, DigitalChannel> digitalChannels() {
        return digitalChannels;
    }

    public DeviceMap<String, OpticalDistanceSensor> opticalDistanceSensors() {
        return opticalDistanceSensors;
    }

    public DeviceMap<String, TouchSensor> touchSensors() {
        return touchSensors;
    }

    public DeviceMap<String, PWMOutput> pwmOutputs() {
        return pwmOutputs;
    }

    public DeviceMap<String, I2cDevice> i2cDevices() {
        return i2cDevices;
    }

    public DeviceMap<String, AnalogOutput> analogOutputs() {
        return analogOutputs;
    }

    public DeviceMap<String, AccelerationSensor> accelerationSensors() {
        return accelerationSensors;
    }

    public DeviceMap<String, CompassSensor> compassSensors() {
        return compassSensors;
    }

    public DeviceMap<String, GyroSensor> gyroSensors() {
        return gyroSensors;
    }

    public DeviceMap<String, IrSeekerSensor> irSeekerSensors() {
        return irSeekerSensors;
    }

    public DeviceMap<String, LightSensor> lightSensors() {
        return lightSensors;
    }

    public DeviceMap<String, UltrasonicSensor> ultrasonicSensors() {
        return ultrasonicSensors;
    }

    public DeviceMap<String, VoltageSensor> voltageSensors() {
        return voltageSensors;
    }

    public class DeviceMap<K, T extends HardwareDevice> extends HashMap<String, T> {
        private TypeToken<T> type = new TypeToken<T>(getClass()) {
        };
        private boolean mock;

        public DeviceMap(HardwareMap.DeviceMapping<T> deviceMapping) {
            super(deviceMapping.size());
            buildFromDeviceMapping(deviceMapping);
        }

        public void enableMocking() {
            throw new IllegalArgumentException("Stub");

            /*if (supportsMocking(type.getRawType())) {
                mock = true;
            } else {
                throw new IllegalArgumentException("Object type does not support mocking");
            }*/
        }

        public void disableMocking() {
            mock = false;
        }

        public DeviceMap<K, T> buildFromDeviceMapping(HardwareMap.DeviceMapping<T> deviceMapping) {
            Set<Entry<String, T>> entries = deviceMapping.entrySet();
            for (Entry<String, T> device : entries) {
                super.put(device.getKey(), device.getValue());
            }
            return this;
        }

        @Override
        @Nullable
        public T get(Object key) {
            T o = super.get(key);
            if (o == null && mock) {
                try {
                    T value = (T) type.getRawType().getConstructors()[0].newInstance("new");
                    put((String) key, value);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    throw new IllegalStateException("Need to create a stub object, but" +
                            "failed to do so: " + ex.toString());
                }
            }

            return o;
        }
    }


}
