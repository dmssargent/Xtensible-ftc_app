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
package org.ftc.opmodes.examples.K9;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * TeleOp Mode <p> Enables control of the robot via the gamepad
 */
public class K9IrSeeker extends OpMode {

    final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster
    final static double HOLD_IR_SIGNAL_STRENGTH = 0.50; // Higher values will cause the robot to follow closer

    double armPosition;
    double clawPosition;

    DcMotor motorRight;
    DcMotor motorLeft;
    Servo claw;
    Servo arm;
    IrSeekerSensor irSeeker;

    /**
     * Constructor
     */
    public K9IrSeeker() {

    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {

		/*
         * Use the hardwareMap to get the dc motors and servos by name.
		 * Note that the names of the devices must match the names used
		 * when you configured your robot and created the configuration file.
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
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        arm = hardwareMap.servo.get("servo_1");
        claw = hardwareMap.servo.get("servo_6");

        // set the starting position of the wrist and claw
        armPosition = 0.1;
        clawPosition = 0.25;

		/*
		 * We also assume that we have a Hitechnic IR Seeker v2 sensor
		 * with a name of "ir_seeker" configured for our robot.
		 */
        irSeeker = hardwareMap.irSeekerSensor.get("ir_seeker");
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {
        double angle = 0.0;
        double strength = 0.0;
        double left, right = 0.0;

        // keep manipulator out of the way.
        arm.setPosition(armPosition);
        claw.setPosition(clawPosition);

		/*
		 * Do we detect an IR signal?
		 */
        if (irSeeker.signalDetected()) {
			/*
			 * Signal was detected. Follow it.
			 */

			/*
			 * Get angle and strength of the signal.
			 * Note an angle of zero implies straight ahead.
			 * A negative angle implies that the source is to the left.
			 * A positive angle implies that the source is to the right.
			 */
            angle = irSeeker.getAngle();
            strength = irSeeker.getStrength();

            if (angle < -60) {
                /*
                 * IR source is to the way left.
                 * Point turn to the left.
                 */
                left = -MOTOR_POWER;
                right = MOTOR_POWER;

            } else if (angle < -5) {
                // turn to the left and move forward.
                left = MOTOR_POWER - 0.05;
                right = MOTOR_POWER;
            } else if (angle > 5 && angle < 60) {
                // turn to the right and move forward.
                left = MOTOR_POWER;
                right = MOTOR_POWER - 0.05;
            } else if (angle > 60) {
                // point turn to right.
                left = MOTOR_POWER;
                right = -MOTOR_POWER;
            } else if (strength < HOLD_IR_SIGNAL_STRENGTH) {
				/*
				 * Signal is dead ahead but weak.
				 * Move forward towards signal
				 */
                left = MOTOR_POWER;
                right = MOTOR_POWER;
            } else {
				/*
				 * Signal is dead ahead and strong.
				 * Stop motors.
				 */
                left = 0.0;
                right = 0.0;
            }
        } else {
			/*
			 * Signal was not detected.
			 * Shut off motors
			 */
            left = 0.0;
            right = 0.0;
        }

		/*
		 * set the motor power
		 */
        motorRight.setPower(right);
        motorLeft.setPower(left);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("angle", "angle:  " + Double.toString(angle));
        telemetry.addData("strength", "sig strength: " + Double.toString(strength));
        telemetry.addData("left tgt pwr", "left  pwr: " + Double.toString(left));
        telemetry.addData("right tgt pwr", "right pwr: " + Double.toString(right));
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

}
