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
package org.ftc.opmodes.statemachine;

import org.ftc.opmodes.examples.pushbot.PushBotHardware;

// PushBotAuto
public class PushBotExampleCase extends PushBotHardware {
    /**
     * This class member remembers which state is currently active.  When the
     * start method is called, the state will be initialized (0).  When the loop
     * starts, the state will change from initialize to state_1.  When state_1
     * actions are complete, the state will change to state_2.  This implements
     * a state machine for the loop method.
     */
    private int v_state = 0;

    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     * <p>
     * The system calls this member once when the OpMode is enabled.
     */
    @Override
    public void start() {
        // Call the PushBotHardware (super/base class) start method.
        super.start();

        // Reset the motor encoders on the drive wheels.
        reset_drive_encoders();
    } // start

    /**
     * Implement a state machine that controls the robot during auto-operation.
     * The state machine uses a class member and encoder input to transition
     * between states.
     * <p>
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override
    public void loop() {
        // StateNode: Initialize (i.e. state_0).
        switch (v_state) {
            // Synchronize the state machine and hardware.
            case 0:
                // Reset the encoders to ensure they are at a known good value.
                reset_drive_encoders();
                // Transition to the next state when this method is called again.
                v_state++;
                break;

            // Drive forward until the encoders exceed the specified values.
            case 1:
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                run_using_encoders();

                // Start the drive wheel motors at full power.
                set_drive_power(1.0f, 1.0f);

                // Have the motor shafts turned the required amount?
                //
                // If they haven't, then the op-mode remains in this state (i.e this
                // block will be executed the next time this method is called).
                if (have_drive_encoders_reached(2880, 2880)) {
                    // Reset the encoders to ensure they are at a known good value.
                    reset_drive_encoders();

                    // Stop the motors.
                    set_drive_power(0.0f, 0.0f);

                    // Transition to the next state when this method is called
                    // again.
                    v_state++;
                }
                break;

            // Wait...
            case 2:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;

            // Turn left until the encoders exceed the specified values.
            case 3:
                run_using_encoders();
                set_drive_power(-1.0f, 1.0f);
                if (have_drive_encoders_reached(2880, 2880)) {
                    reset_drive_encoders();
                    set_drive_power(0.0f, 0.0f);
                    v_state++;
                }
                break;

            // Wait...
            case 4:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;

            // Turn right until the encoders exceed the specified values.
            case 5:
                run_using_encoders();
                set_drive_power(1.0f, -1.0f);
                if (have_drive_encoders_reached(2880, 2880)) {
                    reset_drive_encoders();
                    set_drive_power(0.0f, 0.0f);
                    v_state++;
                }
                break;

            // Wait...
            case 6:
                if (have_drive_encoders_reset()) {
                    v_state++;
                }
                break;

            // Perform no action - stay in this case until the OpMode is stopped.
            // This method will still be called regardless of the state machine.
            default:
                // The autonomous actions have been accomplished (i.e. the state has
                // transitioned into its final state.
                break;
        }

        // Send telemetry data to the driver station.
        telemetry.addData("18", "StateNode: " + v_state);

    } // loop
}
