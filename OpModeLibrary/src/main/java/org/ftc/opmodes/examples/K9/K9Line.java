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
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class K9Line extends OpMode {

	final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster
	final static double HOLD_IR_SIGNAL_STRENGTH = 0.20; // Higher values will cause the robot to follow closer
	final static double LIGHT_THRESHOLD = 0.5;

	double armPosition;
	double clawPosition;

	DcMotor motorRight;
	DcMotor motorLeft;
	Servo claw;
	Servo arm;
	LightSensor reflectedLight;

	/**
	 * Constructor
	 */
	public K9Line() {

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
		 *   "motor_2" is on the left side of the bot..
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
		armPosition = 0.2;
		clawPosition = 0.25;

		/*
		 * We also assume that we have a LEGO light sensor
		 * with a name of "light_sensor" configured for our robot.
		 */
		reflectedLight = hardwareMap.lightSensor.get("light_sensor");

        // turn on LED of light sensor.
        reflectedLight.enableLed(true);
	}

	/*
	 * This method will be called repeatedly in a loop
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {
		double reflection = 0.0;
		double left, right = 0.0;

		// keep manipulator out of the way.
		arm.setPosition(armPosition);
		claw.setPosition(clawPosition);

        /*
         * As a temporary fix, turn on LED in run() event rather than in start().
         */
        // turn on LED of light sensor.
        //reflectedLight.enableLed(true);

		/*
		 * read the light sensor.
		 */
		reflection = reflectedLight.getLightDetected();

		/*
		 * compare measured value to threshold.
		 */
		if (reflection < LIGHT_THRESHOLD) {
			/*
			 * if reflection is less than the threshold value, then assume we are above dark spot.
			 * turn to the right.
			 */
			left = MOTOR_POWER;
			right = 0.0;
		} else {
			/*
			 * assume we are over a light spot.
			 * turn to the left.
			 */
			left = 0.0;
			right = MOTOR_POWER;
		}

		/*
		 * set the motor power
		 */
		motorRight.setPower(left);
		motorLeft.setPower(right);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

		telemetry.addData("Text", "*** Robot Data***");
		telemetry.addData("reflection", "reflection:  " + Double.toString(reflection));
		telemetry.addData("left tgt pwr",  "left  pwr: " + Double.toString(left));
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
