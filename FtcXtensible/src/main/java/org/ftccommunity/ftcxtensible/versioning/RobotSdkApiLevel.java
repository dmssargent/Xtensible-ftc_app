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
package org.ftccommunity.ftcxtensible.versioning;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public enum RobotSdkApiLevel implements Comparator<RobotSdkApiLevel> {
    /**
     * @see #R20150803_001
     * @deprecated use the newer version instead {@link #R20150803_001}
     */
    @Deprecated
    A1_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * @see #R15_10_06_002
     * @deprecated use the newer version instead {@link #R15_10_06_002}
     */
    @Deprecated R1_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * @see #R15_11_04_001
     * @deprecated use the newer version instead {@link #R15_11_04_001}
     */
    @Deprecated R2_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);

        }
    },
    /**
     * @see #R16_01_04
     * @deprecated use the newer version instead {@link #R16_01_04}
     */
    @Deprecated R3_2015 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * @see #R16_02_09
     * @deprecated use the newer version instead {@link #R16_02_09}
     */
    @Deprecated R4_2015_VER2_10 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * @deprecated
     */
    @Deprecated R5_2015_VER2_25 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * Release 20150803_001
     * Changelog:
     * - New user interfaces for FTC Driver Station and FTC Robot Controller apps.
     * - An init() method is added to the OpMode class.
     * - For this release, init() is triggered right before the start() method.
     * - Eventually, the init() method will be triggered when the user presses an "INIT" button on driver station.
     * - The init() and loop() methods are now required (i.e., need to be overridden in the user's op mode).
     * - The start() and stop() methods are optional.
     * - A new LinearOpMode class is introduced.
     * - Teams can use the LinearOpMode mode to create a linear (not event driven) program model.
     * - Teams can use blocking statements like Thread.sleep() within a linear op mode.
     * - The API for the Legacy Module and Core Device Interface Module have been updated.
     * - Support for encoders with the Legacy Module is now working.
     * - The hardware loop has been updated for better performance.
     *
     * @since 8/03/2015
     */
    R20150803_001 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * Release 10/6/2015
     * <p/>
     * Changelog:
     * - Added support for Legacy Matrix 9.6V motor/servo controller.
     * - Cleaned up build.gradle file.
     * - Minor UI and bug fixes for driver station and robot controller apps.
     * - Throws error if Ultrasonic sensor (NXT) is not configured for legacy module port 4 or 5.
     *
     * @since 10/6/2015
     */
    R15_10_06_002 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     * Release 11/04/2015
     * <p/>
     * Changelog:
     * - Added Support for Modern Robotics Gyro.
     * - The GyroSensor class now supports the MR Gyro Sensor.
     * - Users can access heading data (about Z axis)
     * - Users can also access raw gyro data (X, Y, & Z axes).
     * - Example MRGyroTest.java op mode included.
     * - Improved error messages
     * - More descriptive error messages for exceptions in user code.
     * - Updated  DcMotor API
     * - Enable read mode on new address in setI2cAddress
     * - Fix so that driver station app resets the gamepads when switching op modes.
     * - USB-related code changes to make USB comm more responsive and to display more explicit error messages.
     * - Fix so that USB will recover properly if the USB bus returns garbage data.
     * - Fix USB initializtion race condition.
     * - Better error reporting during FTDI open.
     * - More explicit messages during USB failures.
     * - Fixed bug so that USB device is closed if event loop teardown method was not called.
     * - Fixed timer UI issue
     * - Fixed duplicate name UI bug (Legacy Module configuration).
     * - Fixed race condition in EventLoopManager.
     * - Fix to keep references stable when updating gamepad.
     * - For legacy Matrix motor/servo controllers removed necessity of appending "Motor" and "Servo" to controller names.
     * - Updated HT color sensor driver to use constants from ModernRoboticsUsbLegacyModule class.
     * - Updated MR color sensor driver to use constants from ModernRoboticsUsbDeviceInterfaceModule class.
     * - Correctly handle I2C Address change in all color sensors
     * - Updated/cleaned up op modes.
     * - Updated comments in LinearI2cAddressChange.java example op mode.
     * - Replaced the calls to "setChannelMode" with "setMode" (to match the new of the DcMotor method).
     * - Removed K9AutoTime.java op mode.
     * - Added MRGyroTest.java op mode (demonstrates how to use MR Gyro Sensor).
     * - Added MRRGBExample.java op mode (demonstrates how to use MR Color Sensor).
     * - Added HTRGBExample.java op mode (demonstrates how to use HT legacy color sensor).
     * - Added MatrixControllerDemo.java (demonstrates how to use legacy Matrix controller).
     * - Updated javadoc documentation.
     * - Updated release .apk files for Robot Controller and Driver Station apps.
     */
    R15_11_04_001 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     *
     */
    R16_01_04 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     *
     */
    R16_02_09 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    },
    /**
     *
     */
    R16_03_09 {
        @Override
        public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
            return comparator(lhs, rhs);
        }
    };


    @NotNull
    @Contract(pure = true)
    public static RobotSdkApiLevel currentVersion() {
        return R16_03_09;
    }

    int comparator(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
        return compare(lhs, rhs);
    }

    public int compare(RobotSdkApiLevel lhs, RobotSdkApiLevel rhs) {
        int leftHandSide = lhs.ordinal();
        int rightHandSide = rhs.ordinal();

        if (leftHandSide > rightHandSide) {
            return -1;
        } else if (leftHandSide == rightHandSide) {
            return 0;
        } else {
            return 1;
        }
    }
}
