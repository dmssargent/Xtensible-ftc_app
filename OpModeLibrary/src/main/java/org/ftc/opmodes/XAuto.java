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

package org.ftc.opmodes;

import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * A demo GamePad recording OpMode
 */
public class XAuto extends TeamClutchDrive {
    //protected SamplingExecutor<UltrasonicSensor> ultrasonicSensorSamplingExecutor;
    protected final String GAMEPAD_RECORD_NAME = "AutoGamepad";
    protected UltrasonicSensor ultrasonicSensor;
    protected AdafruitI2cColorSensor colorSensor;

//    @Override
//    public void init(RobotContext ctx) throws Exception {
//        robotInit();
//        configureAutoDevices();
//    }

    protected void configureAutoDevices() {
        ultrasonicSensor = hardwareMap.ultrasonicSensors().get("ultrasonic");

        colorSensor = new AdafruitI2cColorSensor(hardwareMap.deviceInterfaceModules().get("Device Interface Module 1"), 0);
        //colorSensor.enableLed(false);


        //ultrasonicSensorSamplingExecutor = new SamplingExecutor<>(ultrasonicSensor, new Numericalize<UltrasonicSensor>() {
        //    @Override
        //    public double toNumber(UltrasonicSensor object) {
        //        return object.getUltrasonicLevel();
        //    }
        //}, 100, TimeUnit.MILLISECONDS);
    }

//    @Override
//    public void loop(RobotContext ctx) throws Exception {
////        if (ultrasonicSensorSamplingExecutor.predictedMean() > 20) {
////            super.loop(ctx);
////        } else {
////            stopRobot();
////        }void
//        robotDrive();
//        autoTelemetry();
//    }

    protected void autoTelemetry() {
        if (ultrasonicSensor != null) {
            //telemetry.data("STAT_ULTRASONIC", ultrasonicSensorSamplingExecutor.predictedMean());
            telemetry.data("ULTRASONIC", ultrasonicSensor.getUltrasonicLevel());
        }

        telemetry.data("RECORDING", gamepad1.isRecording() ? "RECORDING" : "PLAYING BACK");
        telemetry.data("COLOR", colorSensor.red() > colorSensor.blue() ? "red" : "blue");
    }

    protected void stopRobot() {
        left0.setPower(0);
        left1.setPower(0);

        right0.setPower(0);
        right1.setPower(0);
    }

//    @Override
//    public void stop(RobotContext ctx, LinkedList<Object> objects) throws Exception {
//        robotStop(ctx, objects);
//    }
}
