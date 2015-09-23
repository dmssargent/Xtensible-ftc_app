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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Linear Tele Op Mode
 * <p/>
 * Enables control of the robot via the gamepad.
 * NOTE: This op mode will not work with the NXT Motor Controllers. Use an Nxt op mode instead.
 */
@Disabled
@TeleOp(pairWithAuto = "examples")
public class LinearK9TeleOp extends LinearOpMode {

    // position of the neck servo
    double neckPosition;
    double jawPosition;

    // amount to change the neck servo position by
    double neckDelta = 0.01;

    DcMotor motorRight;
    DcMotor motorLeft;

    Servo neck;
    Servo jaw;

    @Override
    public void runOpMode() throws InterruptedException {
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");
        neck = hardwareMap.servo.get("servo_1");
        jaw = hardwareMap.servo.get("servo_6");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // set the starting position of the wrist and neck
        neckPosition = 0.5;

        waitForStart();

        while (opModeIsActive()) {
            // throttle:  left_stick_y ranges from -1 to 1, where -1 is full up,  and 1 is full down
            // direction: left_stick_x ranges from -1 to 1, where -1 is full left and 1 is full right
            float throttle = -gamepad1.left_stick_y;
            float direction = gamepad1.left_stick_x;
            float right = throttle - direction;
            float left = throttle + direction;

            // clip the right/left values so that the values never exceed +/- 1
            right = Range.clip(right, -1, 1);
            left = Range.clip(left, -1, 1);

            // write the values to the motors
            motorRight.setPower(right);
            motorLeft.setPower(left);

            // update the position of the neck
            if (gamepad1.y) {
                neckPosition -= neckDelta;
            }

            if (gamepad1.a) {
                neckPosition += neckDelta;
            }

            // clip the position values so that they never exceed 0..1
            neckPosition = Range.clip(neckPosition, 0, 1);

            // set jaw position
            jawPosition = 1 - Range.scale(gamepad1.right_trigger, 0.0, 1.0, 0.3, 1.0);

            // write position values to the wrist and neck servo
            neck.setPosition(neckPosition);
            jaw.setPosition(jawPosition);

            telemetry.addData("Text", "K9TeleOp");
            telemetry.addData(" left motor", motorLeft.getPower());
            telemetry.addData("right motor", motorRight.getPower());
            telemetry.addData("neck", neck.getPosition());
            telemetry.addData("jaw", jaw.getPosition());

            waitOneHardwareCycle();
        }
    }
}
