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

//------------------------------------------------------------------------------
//
// PushBotTelemetry
//


import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Extends the PushBotHardware class to provide basic telemetry for the Push
 * Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-02-13-57
 */
@Disabled
@TeleOp(pairWithAuto = "PushBot")
public class PushBotTelemetry extends PushBotHardware

{
    //--------------------------------------------------------------------------
    //
    // update_telemetry
    //

    /**
     * Update the telemetry with current values from the base class.
     */
    public void update_telemetry()

    {
        //
        // Send telemetry data to the driver station.
        //
        telemetry.addData
                ("01"
                        , "Left Drive: "
                                + a_left_drive_power()
                                + ", "
                                + a_left_encoder_count()
                );
        telemetry.addData
                ("02"
                        , "Right Drive: "
                                + a_right_drive_power()
                                + ", "
                                + a_right_encoder_count()
                );
        telemetry.addData
                ("03"
                        , "Left Arm: " + a_left_arm_power()
                );
        telemetry.addData
                ("04"
                        , "Hand Position: " + a_hand_position()
                );
    } // PushBotTelemetry::loop

} // PushBotTelemetry
