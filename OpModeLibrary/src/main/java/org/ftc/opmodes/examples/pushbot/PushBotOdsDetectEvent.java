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

//------------------------------------------------------------------------------
//
// PushBotOdsDetectEvent
//


/**
 * Provide a basic autonomous operational mode that demonstrates the use of an optical distance
 * sensor to detect a line implemented using a state machine for the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-30-11-45
 */
public class PushBotOdsDetectEvent extends PushBotTelemetrySensors

{
    //--------------------------------------------------------------------------
    //
    // PushBotOdsDetectEvent
    //

    /**
     * Construct the class. <p> The system calls this member when the class is instantiated.
     */
    public PushBotOdsDetectEvent() {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotOdsDetectEvent

    //--------------------------------------------------------------------------
    //
    // loop
    //

    /**
     * Implement a state machine that controls the robot during auto-operation. <p> The system calls
     * this member repeatedly while the OpMode is running.
     */
    @Override
    public void loop() {
        //
        // If a white line has been detected, then set the power level to zero.
        //
        if (a_ods_white_tape_detected()) {
            set_drive_power(0.0, 0.0);
        }
        //
        // Else a white line has not been detected, so set the power level to
        // full forward.
        //
        else {
            set_drive_power(1.0, 1.0);
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry(); // Update common telemetry

    } // loop

} // PushBotOdsDetectEvent
