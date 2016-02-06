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
package org.ftc.opmodes.examples.K9.xtensible;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.interfaces.JoystickScaler;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;

import java.util.LinkedList;

@RobotSdkVersion(RobotSdkApiLevel.R3_2015)
@TeleOp
public class K9TankDrive extends ExtensibleOpMode {
    /*
     * Note: the configuration of the servos is such that
     * as the arm servo approaches 0, the arm position moves up (away from the floor).
     * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
     */
    // TETRIX VALUES.
    final static double ARM_MIN_RANGE = 0.20;
    final static double ARM_MAX_RANGE = 0.90;
    final static double CLAW_MIN_RANGE = 0.20;
    final static double CLAW_MAX_RANGE = 0.7;
    // amount to change the arm servo position.
    private final double armDelta = 0.1;
    // amount to change the claw servo position by
    private final double clawDelta = 0.1;
    private DcMotor motorRight;
    private DcMotor motorLeft;
    private Servo claw;
    private Servo arm;
    // position of the arm servo.
    private double armPosition = 0.2;
    // position of the claw servo
    private double clawPosition = 0.2;

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        /*
         * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

		/*
         * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot.
		 *
		 * We also assume that there are two servos "servo_1" and "servo_6"
		 *    "servo_1" controls the arm joint of the manipulator.
		 *    "servo_6" controls the claw joint of the manipulator.
		 */
        motorRight = hardwareMap().dcMotors().get("motor_2");
        motorLeft = hardwareMap().dcMotors().get("motor_1");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        arm = hardwareMap().servos().get("servo_1");
        claw = hardwareMap().servos().get("servo_6");

        gamepad1().setupJoystickScalers(new JoystickScaler() { // Left Joystick
            @Override
            public double scaleX(ExtensibleGamepad gamepad, double x) {
                return Range.clip(x, -1, 1);
            }

            @Override
            public double scaleY(ExtensibleGamepad gamepad, double y) {
                return scaleInput(y);
            }

            @Override
            public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }

            @Override
            public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }
        }, new JoystickScaler() { // Right Joystick
            @Override
            public double scaleX(ExtensibleGamepad gamepad, double x) {
                return Range.clip(x, -1, 1);
            }

            @Override
            public double scaleY(ExtensibleGamepad gamepad, double y) {
                return scaleInput(y);
            }

            @Override
            public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }

            @Override
            public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }
        });
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        /*
		 * Gamepad 1
		 *
		 * Gamepad 1 controls the motors via the left stick, and it controls the
		 * wrist/claw via the a,b, x, y buttons
		 */

        // tank drive
        // note that if y equal -1 then joystick is pushed all of the way forward.
        double left = -gamepad1().leftJoystick().Y();
        double right = -gamepad1().rightJoystick().Y();

        // write the values to the motors
        motorRight.setPower(right);
        motorLeft.setPower(left);

        // update the position of the arm.
        if (gamepad1().isAPressed()) {
            // if the A button is pushed on gamepad1, increment the position of
            // the arm servo.
            armPosition += armDelta;
        }

        if (gamepad1().isYPressed()) {
            // if the Y button is pushed on gamepad1, decrease the position of
            // the arm servo.
            armPosition -= armDelta;
        }

        // update the position of the claw
        if (gamepad1().isLeftBumperPressed()) {
            clawPosition += clawDelta;
        }

        if (gamepad1().getLeftTrigger() > 0.25) {
            clawPosition -= clawDelta;
        }

        if (gamepad1().isBPressed()) {
            clawPosition -= clawDelta;
        }

        // update the position of the claw
        if (gamepad1().isXPressed()) {
            clawPosition += clawDelta;
        }

        // clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(armPosition, ARM_MIN_RANGE, ARM_MAX_RANGE);
        clawPosition = Range.clip(clawPosition, CLAW_MIN_RANGE, CLAW_MAX_RANGE);

        // write position values to the wrist and claw servo
        arm.setPosition(armPosition);
        claw.setPosition(clawPosition);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
        telemetry().data("Text", "*** Robot Data***");
        telemetry().data("arm", "arm:  " + String.format("%.2f", armPosition));
        telemetry().data("claw", "claw:  " + String.format("%.2f", clawPosition));
        telemetry().data("left tgt pwr", "left  pwr: " + String.format("%.2f", left));
        telemetry().data("right tgt pwr", "right pwr: " + String.format("%.2f", right));
    }


    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }

    /**
     * This method scales the joystick input so for low joystick values, the scaled value is less
     * than linear.  This is to make it easier to drive the robot more precisely at slower speeds.
     */
    private double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }


}
