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
package org.ftc.opmodes.examples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;

/**
 * A simple example of a linear op mode that will approach an IR beacon
 */
public class LinearIrExample extends LinearOpMode {

    final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster
    final static double HOLD_IR_SIGNAL_STRENGTH = 0.20; // Higher values will cause the robot to follow closer

    DcMotor motorRight;
    DcMotor motorLeft;

    IrSeekerSensor irSeeker;

    @Override
    public void runOpMode() throws InterruptedException {

        // set up the hardware devices we are going to use
        irSeeker = hardwareMap.irSeekerSensor.get("ir_seeker");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // wait for the start button to be pressed
        waitForStart();

        // wait for the IR Seeker to detect a signal
        while (!irSeeker.signalDetected()) {
            sleep(1000);
        }

        if (irSeeker.getAngle() < 0) {
            // if the signal is to the left move left
            motorRight.setPower(MOTOR_POWER);
            motorLeft.setPower(-MOTOR_POWER);
        } else if (irSeeker.getAngle() > 0) {
            // if the signal is to the right move right
            motorRight.setPower(-MOTOR_POWER);
            motorLeft.setPower(MOTOR_POWER);
        }

        // wait for the robot to center on the beacon
        while (irSeeker.getAngle() != 0) {
            waitOneFullHardwareCycle();
        }

        // now approach the beacon
        motorRight.setPower(MOTOR_POWER);
        motorLeft.setPower(MOTOR_POWER);

        // wait until we are close enough
        while (irSeeker.getStrength() < HOLD_IR_SIGNAL_STRENGTH) {
            waitOneFullHardwareCycle();
        }

        // gentleStop the motors
        motorRight.setPower(0);
        motorLeft.setPower(0);
    }
}
