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

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.ftccommunity.bindings.DataBinder;

/*
 *
 * This is an example LinearOpMode that shows how to use
 * a legacy (NXT-compatible) Hitechnic Color Sensor v2.
 *
 * The op mode assumes that the color sensor
 * is configured with a name of "nxt".
 *
 * You can use the X button on either gamepad to turn the LED on and off.
 *
 */
public class HTRGBExample extends LinearOpMode {

    ColorSensor sensorRGB;


    @Override
    public void runOpMode() throws InterruptedException {

        // write some device information (connection info, name and type)
        // to the log file.
        hardwareMap.logDevices();

        // get a reference to our ColorSensor object.
        sensorRGB = hardwareMap.colorSensor.get("nxt");

        // bEnabled represents the state of the LED.
        boolean bEnabled = true;

        // turn the LED on in the beginning, just so user will know that the sensor is active.
        sensorRGB.enableLed(true);

        // wait one cycle.
        waitOneFullHardwareCycle();

        // wait for the start button to be pressed.
        waitForStart();

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(DataBinder.getInstance().integers().get(DataBinder.RC_VIEW));

        // bPrevState and bCurrState represent the previous and current state of the button.
        boolean bPrevState = false;
        boolean bCurrState = false;

        // while the op mode is active, loop and read the RGB data.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {
            // check the status of the x button on either gamepad.
            bCurrState = gamepad1.x || gamepad2.x;

            // check for button state transitions.
            if (bCurrState == true && bCurrState != bPrevState) {
                // button is transitioning to a pressed state.

                // print a debug statement.
                DbgLog.msg("MY_DEBUG - x button was pressed!");

                // update previous state variable.
                bPrevState = bCurrState;

                // on button press, enable the LED.
                bEnabled = true;

                // turn on the LED.
                sensorRGB.enableLed(bEnabled);
            } else if (bCurrState == false && bCurrState != bPrevState) {
                // button is transitioning to a released state.

                // print a debug statement.
                DbgLog.msg("MY_DEBUG - x button was released!");

                // update previous state variable.
                bPrevState = bCurrState;

                // on button press, enable the LED.
                bEnabled = false;

                // turn off the LED.
                sensorRGB.enableLed(false);
            }

            // convert the RGB values to HSV values.
            Color.RGBToHSV(sensorRGB.red(), sensorRGB.green(), sensorRGB.blue(), hsvValues);

            // send the info back to driver station using telemetry function.
            telemetry.addData("Clear", sensorRGB.alpha());
            telemetry.addData("Red  ", sensorRGB.red());
            telemetry.addData("Green", sensorRGB.green());
            telemetry.addData("Blue ", sensorRGB.blue());
            telemetry.addData("Hue", hsvValues[0]);

            // change the background color to match the color detected by the RGB sensor.
            // pass a reference to the hue, saturation, and value array as an argument
            // to the HSVToColor method.
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                }
            });

            // wait a hardware cycle before iterating.
            waitOneFullHardwareCycle();
        }
    }
}
