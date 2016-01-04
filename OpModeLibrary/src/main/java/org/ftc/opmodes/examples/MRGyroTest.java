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
import com.qualcomm.robotcore.hardware.GyroSensor;

/*
 *
 * This is an example LinearOpMode that shows how to use
 * the Modern Robotics Gyro.
 *
 * The op mode assumes that the gyro sensor
 * is configured with a name of "gyro".
 *
 *
 *
 */
public class MRGyroTest extends LinearOpMode {


  @Override
  public void runOpMode() throws InterruptedException {

    GyroSensor sensorGyro;
    int xVal, yVal, zVal = 0;
    int heading = 0;

    // write some device information (connection info, name and type)
    // to the log file.
    hardwareMap.logDevices();

    // get a reference to our GyroSensor object.
    sensorGyro = hardwareMap.gyroSensor.get("gyro");

    // calibrate the gyro.
    sensorGyro.calibrate();

    // wait for the start button to be pressed.
    waitForStart();

    // make sure the gyro is calibrated.
    while (sensorGyro.isCalibrating())  {
      Thread.sleep(50);
    }

    while (opModeIsActive())  {
      // if the A and B buttons are pressed, reset Z heading.
      if(gamepad1.a && gamepad1.b)  {
        // reset heading.
        sensorGyro.resetZAxisIntegrator();
      }

      // get the x, y, and z values (rate of change of angle).
      xVal = sensorGyro.rawX();
      yVal = sensorGyro.rawY();
      zVal = sensorGyro.rawZ();

      // get the heading info.
      // the Modern Robotics' gyro sensor keeps
      // track of the current heading for the Z axis only.
      heading = sensorGyro.getHeading();

      telemetry.addData("1. x", String.format("%03d", xVal));
      telemetry.addData("2. y", String.format("%03d", yVal));
      telemetry.addData("3. z", String.format("%03d", zVal));
      telemetry.addData("4. h", String.format("%03d", heading));

      Thread.sleep(100);
    }
  }
}
