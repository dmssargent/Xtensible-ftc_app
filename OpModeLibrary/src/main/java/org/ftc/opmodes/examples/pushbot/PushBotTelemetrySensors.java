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

import com.qualcomm.robotcore.hardware.IrSeekerSensor;

//------------------------------------------------------------------------------
//
// PushBotTelemetrySensors
//

/**
 * Provide telemetry provided by the PushBotHardwareSensors class. <p> Insert this class between a
 * custom op-mode and the PushBotHardwareSensors class to display telemetry available from the
 * hardware sensor class.
 *
 * @author SSI Robotics
 * @version 2015-08-02-13-57 <p> Telemetry Keys 12 - The position of the touch sensor
 *          (true=pressed/false=not pressed). 13 - The angle returned by the IR seeker class, which
 *          indicates the direction of the IR beacon. 14 - The strength of the IR beacon. 14 - The
 *          angle and strength returned by the IR seeker's first internal sensor. 15 - The angle and
 *          strength returned by the IR seeker's second internal sensor. 17 - The value returned by
 *          the optical distance sensor class, which indicates the amount of reflected light
 *          detected by the sensor.
 */
public class PushBotTelemetrySensors extends PushBotHardwareSensors

{
    //--------------------------------------------------------------------------
    //
    // PushBotTelemetrySensors
    //

    /**
     * Construct the class. <p> The system calls this member when the class is instantiated.
     */
    public PushBotTelemetrySensors() {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotTelemetrySensors

    //--------------------------------------------------------------------------
    //
    // update_telemetry
    //

    /**
     * Update the telemetry with current values from the base class.
     */
    public void update_telemetry() {
        //
        // Use a base class method to update telemetry for non-sensor hardware
        // (i.e. left/right drive wheels, left arm, etc.).
        //
        super.update_telemetry();

        //
        // Send telemetry data to the driver station.
        //
        telemetry.addData
                ("12"
                        , "Touch: " + is_touch_sensor_pressed()
                );
        telemetry.addData
                ("13"
                        , "IR Angle: " + a_ir_angle()
                );
        telemetry.addData
                ("14"
                        , "IR Strength: " + a_ir_strength()
                );
        IrSeekerSensor.IrSeekerIndividualSensor[] l_ir_angles_and_strengths
                = a_ir_angles_and_strengths();
        telemetry.addData
                ("15"
                        , "IR Sensor 1: " + l_ir_angles_and_strengths[0].toString()
                );
        telemetry.addData
                ("16"
                        , "IR Sensor 2: " + l_ir_angles_and_strengths[1].toString()
                );
        telemetry.addData
                ("17"
                        , "ODS: " + a_ods_light_detected()
                );

    } // update_telemetry

} // PushBotTelemetrySensors
