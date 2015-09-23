/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftc.opmodes.examples;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;

/**
 * Follow an IR Beacon
 * <p/>
 * How to use: <br>
 * Make sure the Modern Robotics IR beacon is off. <br>
 * Set it to 1200 at 180.  <br>
 * Make sure the side of the beacon with the LED on is facing the robot. <br>
 * Turn on the IR beacon. The robot will now follow the IR beacon. <br>
 * To stop the robot, turn the IR beacon off. <br>
 */
@Disabled
@Autonomous(pairWithTeleOp = "examples")
public class IrSeekerOp extends OpMode {

    final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster

    final static double HOLD_IR_SIGNAL_STRENGTH = 0.20; // Higher values will cause the robot to follow closer

    IrSeekerSensor irSeeker;
    DcMotor motorRight;
    DcMotor motorLeft;

    @Override
    public void init() {
        irSeeker = hardwareMap.irSeekerSensor.get("ir_seeker");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        double angle = 0;
        double strength = 0;

        // Is an IR signal detected?
        if (irSeeker.signalDetected()) {
            // an IR signal is detected

            // Get the angle and strength of the signal
            angle = irSeeker.getAngle();
            strength = irSeeker.getStrength();

            // which direction should we move?
            if (angle < 0) {
                // we need to move to the left
                motorRight.setPower(MOTOR_POWER);
                motorLeft.setPower(-MOTOR_POWER);
            } else if (angle > 0) {
                // we need to move to the right
                motorRight.setPower(-MOTOR_POWER);
                motorLeft.setPower(MOTOR_POWER);
            } else if (strength < HOLD_IR_SIGNAL_STRENGTH) {
                // the IR signal is weak, approach
                motorRight.setPower(MOTOR_POWER);
                motorLeft.setPower(MOTOR_POWER);
            } else {
                // the IR signal is strong, stay here
                motorRight.setPower(0.0);
                motorLeft.setPower(0.0);
            }
        } else {
            // no IR signal is detected
            motorRight.setPower(0.0);
            motorLeft.setPower(0.0);
        }

        telemetry.addData("angle", angle);
        telemetry.addData("strength", strength);

        DbgLog.msg(irSeeker.toString());
    }
}
