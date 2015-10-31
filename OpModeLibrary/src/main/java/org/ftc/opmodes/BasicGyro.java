/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;


public class BasicGyro extends OpMode {
    private static final double MOTOR_POWER = .5;
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private GyroSensor gyro;

    // Keep track of the readings
    // private LinkedList<Double> gyroReadings;
    private long start;
    private double gyroOffest;

    @Override
    public void init() {
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        gyro = hardwareMap.gyroSensor.get("gyro");
        start = System.currentTimeMillis();

        gyroOffest = gyro.getRotation();
        //Range.clip();
        // gyroReadings = new LinkedList<>();
    }

    @Override
    public void loop() {
        final int degreesToTurn = 90;
        //gyroReadings.add(gyro.getRotation());

        // Build the average of the readings
        double avg = gyro.getRotation() - gyroOffest;

        // If we suspect the degrees we have turned (based on the degrees per second) is
        // greater than 90, gentleStop.
        double degreesWeHaveTurned = (avg / 1000) * (System.currentTimeMillis() - start);
        if (degreesWeHaveTurned <= degreesToTurn) {
            motorLeft.setPower(-MOTOR_POWER);
            motorRight.setPower(MOTOR_POWER);
        } else {
            motorLeft.setPower(0f);
            motorRight.setPower(0f);
        }

        telemetry.addData("GYRO", "deg/second: " + avg);
        telemetry.addData("TURN_AMOUNT", "We have possibly turned " + degreesWeHaveTurned);
    }
}
