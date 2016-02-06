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

package org.ftccommunity.i2clibrary;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopHandler;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtServoController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
import com.qualcomm.hardware.modernrobotics.ReadWriteRunnableStandard;
import com.qualcomm.modernrobotics.ReadWriteRunnableUsbHandler;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robot.Robot;

import java.util.concurrent.ExecutorService;

/**
 * Skullduggery we wish we didn't have to do.
 */
public class MemberUtil {
    //----------------------------------------------------------------------------------------------
    // ReadWriteRunnableStandard
    //----------------------------------------------------------------------------------------------

    public static ReadWriteRunnableUsbHandler getHandlerOfReadWriteRunnableStandard(ReadWriteRunnableStandard readWriteRunnableStandard) {
        return Util.getPrivateObjectField(readWriteRunnableStandard, 13);
    }

    public static void setHandlerOfReadWriteRunnableStandard(ReadWriteRunnableStandard readWriteRunnableStandard, ReadWriteRunnableUsbHandler handler) {
        Util.setPrivateObjectField(readWriteRunnableStandard, 13, handler);
    }

    public static void setRunningReadWriteRunnableStandard(ReadWriteRunnableStandard readWriteRunnableStandard, boolean isRunning) {
        Util.setPrivateBooleanField(readWriteRunnableStandard, 6, isRunning);
    }

    //----------------------------------------------------------------------------------------------
    // ReadWriteRunnableUsbHandler
    //----------------------------------------------------------------------------------------------

    public static RobotUsbDevice getRobotUsbDeviceOfReadWriteRunnableUsbHandler(ReadWriteRunnableUsbHandler handler) {
        return Util.getPrivateObjectField(handler, 2);
    }

    //----------------------------------------------------------------------------------------------
    // ModernRoboticsUsbDevice
    //----------------------------------------------------------------------------------------------

    public static ReadWriteRunnableStandard getReadWriteRunnableModernRoboticsUsbDevice(ModernRoboticsUsbDevice device)
    // Here we rely on the fact that ReadWriteRunnableBlocking inherits from ReadWriteRunnableStandard
    {
        return Util.getPrivateObjectField(device, 0);
    }

    public static void setReadWriteRunnableModernRoboticsUsbDevice(ModernRoboticsUsbDevice device, ReadWriteRunnableStandard readWriteRunnableStandard) {
        Util.setPrivateObjectField(device, 0, readWriteRunnableStandard);
    }

    public static void setExecutorServiceModernRoboticsUsbDevice(ModernRoboticsUsbDevice device, ExecutorService service) {
        Util.setPrivateObjectField(device, 1, service);
    }

    public static ExecutorService getExecutorServiceModernRoboticsUsbDevice(ModernRoboticsUsbDevice device) {
        return Util.getPrivateObjectField(device, 1);
    }

    //----------------------------------------------------------------------------------------------
    // FtcRobotControllerService
    //----------------------------------------------------------------------------------------------

    public static Robot robotOfFtcRobotControllerService(FtcRobotControllerService service) {
        return Util.getPrivateObjectField(service, 2 + 7);
    }

    //----------------------------------------------------------------------------------------------
    // FTCEventLoop
    //----------------------------------------------------------------------------------------------

    public static FtcEventLoopHandler handlerOfFtcEventLoop(FtcEventLoop ftcEventLoop) {
        return Util.getPrivateObjectField(ftcEventLoop, 0);
    }

    //----------------------------------------------------------------------------------------------
    // FtcEventLoopHandler
    //----------------------------------------------------------------------------------------------

    public static EventLoopManager eventLoopManagerOfFtcEventLoopHandler(FtcEventLoopHandler ftcEventLoopHandler) {
        return Util.getPrivateObjectField(ftcEventLoopHandler, 0);
    }

    //----------------------------------------------------------------------------------------------
    // EventLoopManager
    //----------------------------------------------------------------------------------------------

    public static RobocolDatagramSocket socketOfEventLoopManager(EventLoopManager manager) {
        return Util.getPrivateObjectField(manager, 2);
    }

    public static void setSocketOfEventLoopManager(EventLoopManager manager, RobocolDatagramSocket socket) {
        Util.setPrivateObjectField(manager, 2, socket);
    }

    public static EventLoopManager.EventLoopMonitor monitorOfEventLoopManager(EventLoopManager manager) {
        return Util.getPrivateObjectField(manager, 8);
    }

    //----------------------------------------------------------------------------------------------
    // Legacy Motor Controller
    //----------------------------------------------------------------------------------------------

    public static boolean isLegacyMotorController(DcMotorController controller) {
        return controller instanceof HiTechnicNxtDcMotorController;
    }

    public static boolean isLegacyServoController(ServoController controller) {
        return controller instanceof HiTechnicNxtServoController;
    }

    public static boolean isModernMotorController(DcMotorController controller) {
        return controller instanceof ModernRoboticsUsbDcMotorController;
    }

    public static boolean isModernServoController(ServoController controller) {
        return controller instanceof ModernRoboticsUsbServoController;
    }

    public static LegacyModule legacyModuleOfLegacyMotorController(DcMotorController controller) {
        return Util.getPrivateObjectField(controller, 0);
    }

    public static int portOfLegacyMotorController(DcMotorController controller) {
        return Util.getPrivateIntField(controller, 5);
    }

    public static LegacyModule legacyModuleOfLegacyServoController(ServoController controller) {
        return Util.getPrivateObjectField(controller, 0);
    }

    public static int portOfLegacyServoController(ServoController controller) {
        return Util.getPrivateIntField(controller, 3);
    }

    public static int i2cAddrOfLegacyMotorController(DcMotorController controller) {
        // From the spec from HiTechnic:
        //
        // "The first motor controller in the daisy chain will use an I2C address of 02/03. Subsequent
        // controllers will obtain addresses of 04/05, 06/07 and 08/09. Only four controllers may be
        // daisy chained."
        //
        // The legacy module appears not to support daisy chaining; it only supports the first
        // address. Note that these are clearly 8-bit addresses, not 7-bit.
        //
        return 0x02;
    }

    public static int i2cAddrOfLegacyServoController(ServoController controller) {
        return 0x02;
    }

    //----------------------------------------------------------------------------------------------
    // DCMotor
    //----------------------------------------------------------------------------------------------

    public static void setControllerOfMotor(DcMotor motor, DcMotorController controller) {
        Util.setPrivateObjectField(motor, 0, controller);
    }

    public static void setControllerOfServo(Servo servo, ServoController controller) {
        Util.setPrivateObjectField(servo, 0, controller);
    }

    //----------------------------------------------------------------------------------------------
    // Color Sensors
    //----------------------------------------------------------------------------------------------

    static DeviceInterfaceModule deviceInterfaceModuleOfAdaFruitColorSensor(ColorSensor sensor) {
        return Util.getPrivateObjectField(sensor, 0);
    }

    static int portOfAdaFruitColorSensor(ColorSensor sensor) {
        return Util.getPrivateIntField(sensor, 5);
    }

    static LegacyModule legacyModuleOfHiTechnicColorSensor(ColorSensor sensor) {
        return Util.getPrivateObjectField(sensor, 0);
    }

    static DeviceInterfaceModule deviceModuleOfModernColorSensor(ColorSensor sensor) {
        return Util.getPrivateObjectField(sensor, 0);
    }

    static int portOfHiTechnicColorSensor(ColorSensor sensor) {
        return Util.getPrivateIntField(sensor, 7);
    }

    static int portOfModernColorSensor(ColorSensor sensor) {
        return Util.getPrivateIntField(sensor, 7);
    }

    //----------------------------------------------------------------------------------------------
    // Legacy Module
    //----------------------------------------------------------------------------------------------

    static I2cController.I2cPortReadyCallback[] callbacksOfLegacyModule(LegacyModule module) {
        return Util.getPrivateObjectField(module, 4);
    }

    //----------------------------------------------------------------------------------------------
    // Device Interface Module
    //----------------------------------------------------------------------------------------------

    static I2cController.I2cPortReadyCallback[] callbacksOfDeviceInterfaceModule(DeviceInterfaceModule module) {
        return Util.getPrivateObjectField(module, 0);
    }

    public static I2cController i2cControllerOfI2cDevice(I2cDevice i2cDevice) {
        return Util.getPrivateObjectField(i2cDevice, 0);
    }

    //----------------------------------------------------------------------------------------------
    // I2cDevice
    //----------------------------------------------------------------------------------------------

    public static int portOfI2cDevice(I2cDevice i2cDevice) {
        return Util.getPrivateIntField(i2cDevice, 1);
    }
}
