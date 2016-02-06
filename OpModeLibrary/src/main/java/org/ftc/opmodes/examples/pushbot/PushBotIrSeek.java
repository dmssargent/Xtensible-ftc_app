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
package org.ftc.opmodes.examples.pushbot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Jordan Burklund on 7/30/2015. An example linear op mode where the pushbot will track
 * an IR beacon.
 */
public class PushBotIrSeek extends LinearOpMode {
    final static double kBaseSpeed = 0.15;  // Higher values will cause the robot to move faster

    final static double kMinimumStrength = 0.08; // Higher values will cause the robot to follow closer
    final static double kMaximumStrength = 0.60; // Lower values will cause the robot to stop sooner

    IrSeekerSensor irSeeker;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        irSeeker = hardwareMap.irSeekerSensor.get("sensor_ir");
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the start button to be pressed
        waitForStart();

        // Continuously track the IR beacon
        while (opModeIsActive()) {
            double angle = irSeeker.getAngle() / 30;  // value between -4...4
            double strength = irSeeker.getStrength();
            if (strength > kMinimumStrength && strength < kMaximumStrength) {
                double leftSpeed = Range.clip(kBaseSpeed + (angle / 8), -1, 1);
                double rightSpeed = Range.clip(kBaseSpeed - (angle / 8), -1, 1);
                leftMotor.setPower(leftSpeed);
                rightMotor.setPower(rightSpeed);
            } else {
                leftMotor.setPower(0);
                rightMotor.setPower(0);
            }
            telemetry.addData("Seeker", irSeeker.toString());
            telemetry.addData("Speed", " Left=" + leftMotor.getPower() + " Right=" + rightMotor.getPower());

            //Wait one hardware cycle to avoid taxing the processor
            waitOneFullHardwareCycle();
        }

    }
}
