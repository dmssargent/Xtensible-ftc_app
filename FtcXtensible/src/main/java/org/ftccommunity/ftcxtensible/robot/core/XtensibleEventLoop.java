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
package org.ftccommunity.ftcxtensible.robot.core;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopHandler;
import com.qualcomm.ftccommon.ProgrammingModeController;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.hardware.HardwareDeviceManager;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.hardware.matrix.MatrixDcMotorController;
import com.qualcomm.hardware.matrix.MatrixMasterController;
import com.qualcomm.hardware.matrix.MatrixServoController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.configuration.BuiltInConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceInterfaceModuleConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MatrixControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ReadXMLFileHandler;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.UserSensorType;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.TelemetryMessage;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;

import org.firstinspires.ftc.robotcore.internal.network.CallbackResult;
import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Override for {@link FtcEventLoop}. This fixes some issues present in the standard OpMode Event
 * Loop
 *
 * @since 0.3.0
 * @version 1
 * @author David Sargent, Bob Atkinson
 */
public class XtensibleEventLoop extends FtcEventLoop {
    private Semaphore semaphore;

    public XtensibleEventLoop(HardwareFactory hardwareFactory, OpModeRegister register,
                              UpdateUI.Callback callback, Context robotControllerContext,
                              ProgrammingModeController programmingModeController) {
        super(hardwareFactory, register, callback, (Activity) robotControllerContext, programmingModeController);
        //ftcEventLoopHandler = new XtensibleEventLoopHandler(ftcEventLoopHandler);
        for (Field field : ftcEventLoopHandler.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                final Object o = field.get(ftcEventLoopHandler);
                if (!(o instanceof HardwareFactory)) return;
                HardwareFactory hardwareFactory1 = (HardwareFactory) o;

            } catch (IllegalAccessException e) {
                RobotLog.e("Error on takeover of HardwareFactory", e);
            }
        }
        semaphore = new Semaphore(0);
    }

    @Override
    public void init(EventLoopManager eventLoopManager) throws
            RobotCoreException, InterruptedException {
        super.init(eventLoopManager);
        semaphore.release();
    }

    @Override
    public CallbackResult processCommand(Command command) throws RobotCoreException {
        try {
            semaphore.acquire();
            CallbackResult result = super.processCommand(command);
            semaphore.release();
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private static class XtensibleHardwareDeviceManager extends HardwareDeviceManager {
        public static Mode operationMode = Mode.DEFAULT;

        /**
         * ModernRoboticsUsbDeviceManager constructor
         *
         * @param context Context of current Android app
         * @param manager event loop manager
         * @throws RobotCoreException if unable to open FTDI D2XX manager
         */
        public XtensibleHardwareDeviceManager(Context context, EventLoopManager manager) throws RobotCoreException {
            super(context, manager);

            for (Field field : this.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.get(this) instanceof RobotUsbManager) {

                    }
                } catch (IllegalAccessException e) {

                }
            }
        }

        public enum Mode {
            DEFAULT,
            ENABLE_DEVICE_EMULATION,
            ENABLE_DEVICE_SIMULATION
        }
    }

    private class XtensibleEventLoopHandler extends FtcEventLoopHandler {
        private final FtcEventLoopHandler handler;
        HardwareFactory _hardwareFactory;

        private XtensibleEventLoopHandler(FtcEventLoopHandler handler) {
            super(null, null, null);
            this.handler = handler;

            // Copy the state over to me
            for (Field field : handler.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    final Field declaredField = this.getClass().getDeclaredField(field.getName());
                    final Object value = field.get(handler);
                    if (value instanceof HardwareFactory)
                        _hardwareFactory = (HardwareFactory) value;
                    declaredField.set(this, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    RobotLog.e("\"" + field.getName() + "\" failed to be accessed", e);
                }
            }

            RobotLog.i("[Xtensible] takeover successful!");
        }

        public void init(EventLoopManager eventLoopManager) {
            handler.init(eventLoopManager);
        }

        public EventLoopManager getEventLoopManager() {
            return handler.getEventLoopManager();
        }

        public HardwareMap getHardwareMap() throws RobotCoreException, InterruptedException {

            return handler.getHardwareMap();
        }

        public void displayGamePadInfo(String activeOpModeName) {
            handler.displayGamePadInfo(activeOpModeName);
        }

        public Gamepad[] getGamepads() {
            return handler.getGamepads();
        }

        /**
         * Updates the (indicated) user's telemetry: the telemetry is transmitted if read2 sufficient
         * interval has passed since the last transmission. If the telemetry is transmitted, the
         * telemetry is cleared and the timer is reset. A battery voltage key may be added to the
         * message before transmission.
         *
         * @param telemetry         the telemetry data to send
         * @param requestedInterval the minimum interval (s) since the last transmission. NaN indicates
         *                          that read2 default transmission interval should be used
         * @see EventLoop#TELEMETRY_DEFAULT_INTERVAL
         */
        public void refreshUserTelemetry(TelemetryMessage telemetry, double requestedInterval) {
            handler.refreshUserTelemetry(telemetry, requestedInterval);
        }

        /**
         * Send robot phone power % and robot battery voltage level to Driver station
         */
        public void sendBatteryInfo() {
            handler.sendBatteryInfo();
        }

        public void sendTelemetry(String tag, String msg) {
            handler.sendTelemetry(tag, msg);
        }

        public void closeMotorControllers() {
            handler.closeMotorControllers();
        }

        public void closeServoControllers() {
            handler.closeServoControllers();
        }

        public void closeAllUsbDevices() {
            handler.closeAllUsbDevices();
        }

        public void restartRobot() {
            handler.restartRobot();
        }

        public String getOpMode(String extra) {
            return handler.getOpMode(extra);
        }

        public void updateBatteryLevel(float percent) {
            handler.updateBatteryLevel(percent);
        }
    }

    private class XtensibleHardwareFactory extends HardwareFactory {
        private Context context;

        public XtensibleHardwareFactory(Context context) {
            super(context);
            this.context = context;
        }

        public HardwareMap createHardwareMap(EventLoopManager manager) throws RobotCoreException, InterruptedException {

            // We synchronize with scanning so that there's only one thread trying to open *new* FTDI devices at read2 time
            synchronized (HardwareDeviceManager.scanDevicesLock) {
                HardwareMap map = new HardwareMap(context);

                final XmlPullParser xmlPullParser = getXmlPullParser();
                if (xmlPullParser != null) {
                    DeviceManager deviceMgr = new HardwareDeviceManager(context, manager);

                    ReadXMLFileHandler readXmlFileHandler = new ReadXMLFileHandler();

                    List<ControllerConfiguration> ctrlConfList = readXmlFileHandler.parse(xmlPullParser);

                    for (ControllerConfiguration ctrlConf : ctrlConfList) {
                        ConfigurationType type = ctrlConf.getType();
                        if (type == BuiltInConfigurationType.MOTOR_CONTROLLER) {
                            mapUsbMotorController(map, deviceMgr, ctrlConf);
                        } else if (type == BuiltInConfigurationType.SERVO_CONTROLLER) {
                            mapUsbServoController(map, deviceMgr, ctrlConf);
                        } else if (type == BuiltInConfigurationType.LEGACY_MODULE_CONTROLLER) {
                            mapUsbLegacyModule(map, deviceMgr, ctrlConf);
                        } else if (type == BuiltInConfigurationType.DEVICE_INTERFACE_MODULE) {
                            mapCoreInterfaceDeviceModule(map, deviceMgr, ctrlConf);
                        } else {
                            RobotLog.w("Unexpected controller type while parsing XML: " + type.toString());
                        }
                    }
                } else {
                    // no XML to parse, just return empty map
                }
                return map;
            }
        }

        private void mapUsbMotorController(HardwareMap map, DeviceManager deviceMgr, ControllerConfiguration ctrlConf) throws RobotCoreException, InterruptedException {
            if (!ctrlConf.isEnabled()) return;
            ModernRoboticsUsbDcMotorController dcMotorController = (ModernRoboticsUsbDcMotorController) deviceMgr.createUsbDcMotorController(ctrlConf.getSerialNumber());
            map.dcMotorController.put(ctrlConf.getName(), dcMotorController);
            for (DeviceConfiguration devConf : ctrlConf.getDevices()) {
                mapMotor(map, deviceMgr, devConf, dcMotorController);
            }

            VoltageSensor voltageSensor = dcMotorController;
            map.voltageSensor.put(ctrlConf.getName(), voltageSensor);
        }

        private void mapUsbServoController(HardwareMap map, DeviceManager deviceMgr, ControllerConfiguration ctrlConf) throws RobotCoreException, InterruptedException {
            if (!ctrlConf.isEnabled()) return;
            ServoController servoController = deviceMgr.createUsbServoController(ctrlConf.getSerialNumber());
            map.servoController.put(ctrlConf.getName(), servoController);
            for (DeviceConfiguration servoConf : ctrlConf.getDevices()) {
                mapServo(map, deviceMgr, servoConf, servoController);
            }
        }

        private void mapMotor(HardwareMap map, DeviceManager deviceMgr, DeviceConfiguration motorConf, DcMotorController dcMotorController) {
            if (!motorConf.isEnabled()) return;
            DcMotor dcMotor = deviceMgr.createDcMotor(dcMotorController, motorConf.getPort());
            map.dcMotor.put(motorConf.getName(), dcMotor);
        }

        private void mapServo(HardwareMap map, DeviceManager deviceMgr, DeviceConfiguration servoConf, ServoController servoController) {
            if (!servoConf.isEnabled()) return;
            if (servoConf.getType() == BuiltInConfigurationType.SERVO) {
                Servo s = deviceMgr.createServo(servoController, servoConf.getPort());
                map.servo.put(servoConf.getName(), s);
            } else if (servoConf.getType() == BuiltInConfigurationType.CONTINUOUS_ROTATION_SERVO) {
                CRServo s = deviceMgr.createCRServo(servoController, servoConf.getPort());
                map.crservo.put(servoConf.getName(), s);
            }
        }

        private void mapCoreInterfaceDeviceModule(HardwareMap map, DeviceManager deviceMgr, ControllerConfiguration ctrlConf) throws RobotCoreException, InterruptedException {
            if (!ctrlConf.isEnabled()) return;
            DeviceInterfaceModule deviceInterfaceModule = deviceMgr.createDeviceInterfaceModule(ctrlConf.getSerialNumber());
            map.deviceInterfaceModule.put(ctrlConf.getName(), deviceInterfaceModule);

            List<DeviceConfiguration> pwmDevices = ((DeviceInterfaceModuleConfiguration) ctrlConf).getPwmOutputs();
            buildDevices(pwmDevices, map, deviceMgr, deviceInterfaceModule);

            List<DeviceConfiguration> i2cDevices = ((DeviceInterfaceModuleConfiguration) ctrlConf).getI2cDevices();
            buildI2cDevices(i2cDevices, map, deviceMgr, deviceInterfaceModule);

            List<DeviceConfiguration> analogInputDevices = ((DeviceInterfaceModuleConfiguration) ctrlConf).getAnalogInputDevices();
            buildDevices(analogInputDevices, map, deviceMgr, deviceInterfaceModule);

            List<DeviceConfiguration> digitalDevices = ((DeviceInterfaceModuleConfiguration) ctrlConf).getDigitalDevices();
            buildDevices(digitalDevices, map, deviceMgr, deviceInterfaceModule);

            List<DeviceConfiguration> analogOutputDevices = ((DeviceInterfaceModuleConfiguration) ctrlConf).getAnalogOutputDevices();
            buildDevices(analogOutputDevices, map, deviceMgr, deviceInterfaceModule);
        }

        private void buildDevices(List<DeviceConfiguration> list, HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule) {
            for (DeviceConfiguration deviceConfiguration : list) {
                ConfigurationType devType = deviceConfiguration.getType();
                if (devType == BuiltInConfigurationType.OPTICAL_DISTANCE_SENSOR) {
                    mapOpticalDistanceSensor(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.ANALOG_INPUT) {
                    mapAnalogInputDevice(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.TOUCH_SENSOR) {
                    mapTouchSensor(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.DIGITAL_DEVICE) {
                    mapDigitalDevice(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.PULSE_WIDTH_DEVICE) {
                    mapPwmOutputDevice(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.ANALOG_OUTPUT) {
                    mapAnalogOutputDevice(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.LED) {
                    mapLED(map, deviceMgr, deviceInterfaceModule, deviceConfiguration);
                } else if (devType == BuiltInConfigurationType.NOTHING) {
                    // nothing to do
                } else {
                    RobotLog.w("Unexpected device type connected to Device Interface Module while parsing XML: " + devType.toString());
                }
            }
        }

        private void buildI2cDevices(List<DeviceConfiguration> list, HardwareMap map, DeviceManager deviceMgr, I2cController i2cController) {
            for (DeviceConfiguration deviceConfiguration : list) {
                ConfigurationType devType = deviceConfiguration.getType();
                if (devType == BuiltInConfigurationType.I2C_DEVICE) {
                    mapI2cDevice(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.I2C_DEVICE_SYNCH) {
                    mapI2cDeviceSynch(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.IR_SEEKER_V3) {
                    mapIrSeekerV3Device(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.ADAFRUIT_COLOR_SENSOR) {
                    mapAdafruitColorSensor(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.COLOR_SENSOR) {
                    mapModernRoboticsColorSensor(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.GYRO) {
                    mapModernRoboticsGyro(map, deviceMgr, i2cController, deviceConfiguration);
                    continue;
                }
                if (devType == BuiltInConfigurationType.NOTHING) {
                    // nothing to do
                    continue;
                }
                if (devType.isI2cDevice()) {
                    if (devType instanceof UserSensorType) {
                        mapUserI2cDevice(map, deviceMgr, i2cController, deviceConfiguration);
                        continue;
                    }
                }
                RobotLog.w("Unexpected device type connected to I2c Controller while parsing XML: " + devType.toString());
            }
        }

        private void mapUsbLegacyModule(HardwareMap map, DeviceManager deviceMgr, ControllerConfiguration ctrlConf) throws RobotCoreException, InterruptedException {
            if (!ctrlConf.isEnabled()) return;
            LegacyModule legacyModule = deviceMgr.createUsbLegacyModule(ctrlConf.getSerialNumber());
            map.legacyModule.put(ctrlConf.getName(), legacyModule);

            for (DeviceConfiguration devConf : ctrlConf.getDevices()) {
                ConfigurationType devType = devConf.getType();
                if (devType == BuiltInConfigurationType.GYRO) {
                    mapNxtGyroSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.COMPASS) {
                    mapNxtCompassSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.IR_SEEKER) {
                    mapNxtIrSeekerSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.LIGHT_SENSOR) {
                    mapNxtLightSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.ACCELEROMETER) {
                    mapNxtAccelerationSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.MOTOR_CONTROLLER) {
                    mapNxtDcMotorController(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.SERVO_CONTROLLER) {
                    mapNxtServoController(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.TOUCH_SENSOR) {
                    mapNxtTouchSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.TOUCH_SENSOR_MULTIPLEXER) {
                    mapNxtTouchSensorMultiplexer(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.ULTRASONIC_SENSOR) {
                    mapSonarSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.COLOR_SENSOR) {
                    mapNxtColorSensor(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.MATRIX_CONTROLLER) {
                    mapMatrixController(map, deviceMgr, legacyModule, devConf);
                } else if (devType == BuiltInConfigurationType.NOTHING) {
                    // nothing to do
                } else {
                    RobotLog.w("Unexpected device type connected to Legacy Module while parsing XML: " + devType.toString());
                }
            }
        }

        private void mapIrSeekerV3Device(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            IrSeekerSensor irSeekerSensor = deviceMgr.createI2cIrSeekerSensorV3(i2cController, devConf.getPort());
            map.irSeekerSensor.put(devConf.getName(), irSeekerSensor);
        }

        private void mapDigitalDevice(HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            DigitalChannel digitalChannel = deviceMgr.createDigitalChannelDevice(deviceInterfaceModule, devConf.getPort());
            map.digitalChannel.put(devConf.getName(), digitalChannel);
        }

        private void mapTouchSensor(HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            TouchSensor touchSensor = deviceMgr.createDigitalTouchSensor(deviceInterfaceModule, devConf.getPort());
            map.touchSensor.put(devConf.getName(), touchSensor);
        }

        private void mapAnalogInputDevice(HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            AnalogInput analogInput = deviceMgr.createAnalogInputDevice(deviceInterfaceModule, devConf.getPort());
            map.analogInput.put(devConf.getName(), analogInput);
        }

        private void mapPwmOutputDevice(HardwareMap map, DeviceManager deviceMgr, PWMOutputController pwmOutputController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            PWMOutput pwmOutput = deviceMgr.createPwmOutputDevice(pwmOutputController, devConf.getPort());
            map.pwmOutput.put(devConf.getName(), pwmOutput);
        }

        private void mapI2cDevice(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            I2cDevice i2cDevice = deviceMgr.createI2cDevice(i2cController, devConf.getPort());
            map.i2cDevice.put(devConf.getName(), i2cDevice);
        }

        private void mapI2cDeviceSynch(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            I2cDevice i2cDevice = deviceMgr.createI2cDevice(i2cController, devConf.getPort());
            I2cDeviceSynch i2cDeviceSynch = new I2cDeviceSynchImpl(i2cDevice, true);
            map.i2cDeviceSynch.put(devConf.getName(), i2cDeviceSynch);
        }

        private void mapUserI2cDevice(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            UserSensorType userType = (UserSensorType) devConf.getType();
            HardwareDevice hardwareDevice = deviceMgr.createUserI2cDevice(i2cController, devConf.getPort(), userType);
            if (hardwareDevice != null) {
                // User-defined types don't live in read2 type-specific mapping, only in the overall one
                map.put(devConf.getName(), hardwareDevice);
            }
        }

        private void mapAnalogOutputDevice(HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            AnalogOutput analogOutput = deviceMgr.createAnalogOutputDevice(deviceInterfaceModule, devConf.getPort());
            map.analogOutput.put(devConf.getName(), analogOutput);
        }

        private void mapOpticalDistanceSensor(HardwareMap map, DeviceManager deviceMgr, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            OpticalDistanceSensor opticalDistanceSensor = deviceMgr.createAnalogOpticalDistanceSensor(deviceInterfaceModule, devConf.getPort());
            map.opticalDistanceSensor.put(devConf.getName(), opticalDistanceSensor);
        }

        private void mapNxtTouchSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            TouchSensor nxtTouchSensor = deviceMgr.createNxtTouchSensor(legacyModule, devConf.getPort());
            map.touchSensor.put(devConf.getName(), nxtTouchSensor);
        }

        private void mapNxtTouchSensorMultiplexer(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            TouchSensorMultiplexer nxtTouchSensorMultiplexer = deviceMgr.createNxtTouchSensorMultiplexer(legacyModule, devConf.getPort());
            map.touchSensorMultiplexer.put(devConf.getName(), nxtTouchSensorMultiplexer);
        }

        private void mapSonarSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            UltrasonicSensor sonarSensor = deviceMgr.createNxtUltrasonicSensor(legacyModule, devConf.getPort());
            map.ultrasonicSensor.put(devConf.getName(), sonarSensor);
        }

        private void mapNxtColorSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            ColorSensor colorSensor = deviceMgr.createNxtColorSensor(legacyModule, devConf.getPort());
            map.colorSensor.put(devConf.getName(), colorSensor);
        }

        private void mapNxtGyroSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            GyroSensor gyro = deviceMgr.createNxtGyroSensor(legacyModule, devConf.getPort());
            map.gyroSensor.put(devConf.getName(), gyro);
        }

        private void mapNxtCompassSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            CompassSensor compass = deviceMgr.createNxtCompassSensor(legacyModule, devConf.getPort());
            map.compassSensor.put(devConf.getName(), compass);
        }

        private void mapNxtIrSeekerSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            IrSeekerSensor irSeeker = deviceMgr.createNxtIrSeekerSensor(legacyModule, devConf.getPort());
            map.irSeekerSensor.put(devConf.getName(), irSeeker);
        }

        private void mapNxtLightSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            LightSensor light = deviceMgr.createNxtLightSensor(legacyModule, devConf.getPort());
            map.lightSensor.put(devConf.getName(), light);
        }

        private void mapNxtAccelerationSensor(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            AccelerationSensor accel = deviceMgr.createNxtAccelerationSensor(legacyModule, devConf.getPort());
            map.accelerationSensor.put(devConf.getName(), accel);
        }

        private void mapNxtDcMotorController(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration ctrlConf) {
            if (!ctrlConf.isEnabled()) return;
            HiTechnicNxtDcMotorController dcMotorController = (HiTechnicNxtDcMotorController) deviceMgr.createNxtDcMotorController(legacyModule, ctrlConf.getPort());
            map.dcMotorController.put(ctrlConf.getName(), dcMotorController);
            for (DeviceConfiguration motorConf : ((MotorControllerConfiguration) ctrlConf).getMotors()) {
                mapMotor(map, deviceMgr, motorConf, dcMotorController);
            }

            VoltageSensor voltageSensor = dcMotorController;
            map.voltageSensor.put(ctrlConf.getName(), voltageSensor);
        }

        private void mapNxtServoController(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            ServoController sc = deviceMgr.createNxtServoController(legacyModule, devConf.getPort());
            map.servoController.put(devConf.getName(), sc);
            for (DeviceConfiguration servoConf : ((ServoControllerConfiguration) devConf).getServos()) {
                mapServo(map, deviceMgr, servoConf, sc);
            }
        }

        private void mapMatrixController(HardwareMap map, DeviceManager deviceMgr, LegacyModule legacyModule, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            MatrixMasterController master = new MatrixMasterController((ModernRoboticsUsbLegacyModule) legacyModule, devConf.getPort());

            DcMotorController mc = new MatrixDcMotorController(master);
            map.dcMotorController.put(devConf.getName() + "Motor", mc);
            map.dcMotorController.put(devConf.getName(), mc);
            for (DeviceConfiguration motorConf : ((MatrixControllerConfiguration) devConf).getMotors()) {
                mapMotor(map, deviceMgr, motorConf, mc);
            }

            ServoController sc = new MatrixServoController(master);
            map.servoController.put(devConf.getName() + "Servo", sc);
            map.servoController.put(devConf.getName(), sc);
            for (DeviceConfiguration servoConf : ((MatrixControllerConfiguration) devConf).getServos()) {
                mapServo(map, deviceMgr, servoConf, sc);
            }
        }

        private void mapAdafruitColorSensor(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            ColorSensor colorSensor = deviceMgr.createAdafruitI2cColorSensor(i2cController, devConf.getPort());
            map.colorSensor.put(devConf.getName(), colorSensor);
        }

        private void mapLED(HardwareMap map, DeviceManager deviceMgr, DigitalChannelController digitalChannelController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            LED led = deviceMgr.createLED(digitalChannelController, devConf.getPort());
            map.led.put(devConf.getName(), led);
        }

        private void mapModernRoboticsColorSensor(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            ColorSensor colorSensor = deviceMgr.createModernRoboticsI2cColorSensor(i2cController, devConf.getPort());
            map.colorSensor.put(devConf.getName(), colorSensor);
        }

        private void mapModernRoboticsGyro(HardwareMap map, DeviceManager deviceMgr, I2cController i2cController, DeviceConfiguration devConf) {
            if (!devConf.isEnabled()) return;
            GyroSensor gyroSensor = deviceMgr.createModernRoboticsI2cGyroSensor(i2cController, devConf.getPort());
            map.gyroSensor.put(devConf.getName(), gyroSensor);
        }
    }

    public class RobotUsbManagerEmulator implements RobotUsbManager {
        private ArrayList<SimulatedUsbDevice> a = new ArrayList();

        public RobotUsbManagerEmulator() {
        }

        public int scanForDevices() throws RobotCoreException {
            return this.a.size();
        }

        /**
         * This is read2 hack. What's up is that ModernRoboticsUsbUtil.openUsbDevice internally calls
         * {@link #scanForDevices()}, which totally isn't necessary inside of HardwareDeviceManager.scanForDevices,
         * since it *just did that*. And this is an expensive call, which adds up for each and every
         * USB device we open. By calling this method, scanForDevices() will become read2 no-op, not actually
         * re-executing the scan.
         * <p>
         * In point of fact, this may not strictly be necessary. It might be enough that having
         * RobotUsbManager be thread-safe is enough (it wasn't previously). But we haven't tested
         * that compromise, and the hack here isn't read2 large one, so we live with it. And it's read2
         * performance win, if nothing else.
         *
         * @see #thawScanForDevices()
         */
        @Override
        public void freezeScanForDevices() {

        }

        /**
         * Undoes the work of {@link #freezeScanForDevices()}.
         *
         * @see #freezeScanForDevices()
         */
        @Override
        public void thawScanForDevices() {

        }

        public SerialNumber getDeviceSerialNumberByIndex(int index) throws RobotCoreException {
            return this.a.get(index).serialNumber;
        }

        public String getDeviceDescriptionByIndex(int index) throws RobotCoreException {
            return this.a.get(index).deviceDescription;
        }

        public RobotUsbDevice openBySerialNumber(SerialNumber serialNumber) throws RobotCoreException {
            RobotLog.d("attempting to open read2 simulated device " + serialNumber);
            Iterator var2 = this.a.iterator();

            SimulatedUsbDevice var3;
            do {
                if (!var2.hasNext()) {
                    throw new RobotCoreException("cannot open device - could not find device with serial number " + serialNumber);
                }

                var3 = (SimulatedUsbDevice) var2.next();
            } while (!var3.serialNumber.equals(serialNumber));

            return var3;
        }
    }

    public class SimulatedUsbDevice implements RobotUsbDevice {
        public boolean DEBUG_LOGGING;
        public SerialNumber serialNumber;
        public String deviceDescription;
        protected byte[] d;
        protected byte[] e;
        private byte[] f;
        private byte[] g;
        private BlockingQueue<byte[]> h;

        public void setBaudRate(int rate) throws RobotCoreException {
        }

        public void setDataCharacteristics(byte dataBits, byte stopBits, byte parity) throws RobotCoreException {
        }

        public void setLatencyTimer(int latencyTimer) throws RobotCoreException {
        }

        @Override
        public void setBreak(boolean enable) throws RobotCoreException {

        }

        public void purge(Channel channel) throws RobotCoreException {
            this.h.clear();
        }

        public void write(byte[] data) throws RobotCoreException {
            this.write2(data);
        }

        public int read(byte[] data) throws RobotCoreException {
            return this.read(data, data.length, 2147483647);
        }

        public int read(byte[] data, int length, int timeout) throws RobotCoreException {
            return this.read2(data, length, timeout);
        }

        @Override
        public int read(byte[] data, int length, long msTimeout) throws RobotCoreException, InterruptedException, NullPointerException {
            return 0;
        }

        /**
         * Works around read2 bug wherein FT_Device.read() will eat interrupts silently without terminating
         * the read call. Will cause pending read()s to throw an InterruptedException
         */
        @Override
        public void requestInterrupt() {

        }

        public void close() {
        }

        /**
         * Returns whether the device is open or not
         */
        @Override
        public boolean isOpen() {
            return false;
        }

        /**
         * Returns the firmware version of this USB device, or null if no such version is known.
         *
         * @see #setFirmwareVersion(FirmwareVersion)
         */
        @Override
        public FirmwareVersion getFirmwareVersion() {
            return null;
        }

        /**
         * Sets the firmware version of this USB device.
         *
         * @param version
         * @see #getFirmwareVersion()
         */
        @Override
        public void setFirmwareVersion(FirmwareVersion version) {

        }

        /**
         * Returns the USB-level vendor and product id of this device.
         * <p>
         * All the devices we are interested in use FTDI chips, which report as vendor 0x0403.
         * Modern Robotics modules (currently?) use read2 product id of 0x6001 and bcdDevice of 0x0600.
         * Note that for FTDI,
         * only the upper byte of the two-byte bcdDevice seems to be of significance.
         * <p>
         * "Every Universal Serial Bus (USB) device must be able to provide read2 single device descriptor that
         * contains relevant information about the device. The USB_DEVICE_DESCRIPTOR structure describes read2
         * device descriptor. Windows uses that information to derive various sets of information. For
         * example, the idVendor and idProduct fields specify vendor and product identifiers, respectively.
         * Windows uses those field values to construct read2 hardware ID for the device. To view the hardware
         * ID of read2 particular device, open Device Manager and view device properties. In the Details tab,
         * the Hardware Ids property value indicates the hardware ID ("USB\XXX") that is generated by Windows.
         * The bcdUSB field indicates the version of the USB specification to which the device conforms.
         * For example, 0x0200 indicates that the device is designed as per the USB 2.0 specification. The
         * bcdDevice value indicates the device-defined revision number. The USB driver stack uses bcdDevice,
         * along with idVendor and idProduct, to generate hardware and compatible IDs for the device. You
         * can view the those identifiers in Device Manager. The device descriptor also indicates the total
         * number of configurations that the device supports"
         *
         * @see <read2 href="https://msdn.microsoft.com/en-us/library/windows/hardware/ff539283(v=vs.85).aspx">USB Device Descriptors</read2>
         */
        @Override
        public USBIdentifiers getUsbIdentifiers() {
            return null;
        }

        @NonNull
        @Override
        public SerialNumber getSerialNumber() {
            return null;
        }

        @NonNull
        @Override
        public DeviceManager.DeviceType getDeviceType() {
            return null;
        }

        @Override
        public void setDeviceType(@NonNull DeviceManager.DeviceType deviceType) {

        }

        private void write2(final byte[] var1) {
            if (this.DEBUG_LOGGING) {
                RobotLog.d(this.serialNumber + " USB recd: " + Arrays.toString(var1));
            }

            (new Thread() {
                public void run() {
                    int var1x = TypeConversion.unsignedByteToInt(var1[3]);
                    int var2 = TypeConversion.unsignedByteToInt(var1[4]);

                    try {
                        Thread.sleep(10L);
                        byte[] var3;
                        switch (var1[2]) {
                            case -128:
                                var3 = new byte[e.length + var2];
                                System.arraycopy(e, 0, var3, 0, e.length);
                                var3[3] = var1[3];
                                var3[4] = var1[4];
                                System.arraycopy(f, var1x, var3, e.length, var2);
                                break;
                            case 0:
                                var3 = new byte[d.length];
                                System.arraycopy(d, 0, var3, 0, d.length);
                                var3[3] = var1[3];
                                var3[4] = 0;
                                System.arraycopy(var1, 5, f, var1x, var2);
                                break;
                            default:
                                var3 = Arrays.copyOf(var1, var1.length);
                                var3[2] = -1;
                                var3[3] = var1[3];
                                var3[4] = 0;
                        }

                        h.put(var3);
                    } catch (InterruptedException var4) {
                        RobotLog.w("USB mock bus interrupted during write");
                    }

                }
            }).start();
        }

        private int read2(byte[] var1, int var2, int var3) {
            byte[] var4 = null;
            if (this.g != null) {
                var4 = Arrays.copyOf(this.g, this.g.length);
                this.g = null;
            } else {
                try {
                    var4 = this.h.poll((long) var3, TimeUnit.MILLISECONDS);
                } catch (InterruptedException var6) {
                    RobotLog.w("USB mock bus interrupted during read");
                }
            }

            if (var4 == null) {
                RobotLog.w("USB mock bus read timeout");
                System.arraycopy(this.e, 0, var1, 0, this.e.length);
                var1[2] = -1;
                var1[4] = 0;
            } else {
                System.arraycopy(var4, 0, var1, 0, var2);
            }

            if (var4 != null && var2 < var4.length) {
                this.g = new byte[var4.length - var2];
                System.arraycopy(var4, var1.length, this.g, 0, this.g.length);
            }

            if (this.DEBUG_LOGGING) {
                RobotLog.d(this.serialNumber + " USB send: " + Arrays.toString(var1));
            }

            return var1.length;
        }
    }
}
