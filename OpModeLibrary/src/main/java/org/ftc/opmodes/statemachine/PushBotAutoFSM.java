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

public class PushBotAutoFSM extends PushBotHardware {
    /**
     * A workaround to access this outer class from the state machine
     */
    private static PushBotAutoFSM me;
    /**
     * This class member remembers which state is currently active.  \
     */
    private FiniteStateMachine<States, PushBotAutoFSM> fsm;

    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     * <p>
     * The system calls this member once when the OpMode is enabled.
     */
    @Override
    public void start() {
        // Call the PushBotHardware (super/base class) start method.
        super.start();

        me = this;

        // Reset the motor encoders on the drive wheels.
        reset_drive_encoders();
        fsm = new FiniteStateMachine<States, PushBotAutoFSM>(States.SYNC_HARDWARE, this);
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
        fsm.execute();

        // Send telemetry data to the driver station.
        telemetry.addData("18", "StateNode: " + fsm.state().name());

    } // loop

    enum States implements FiniteStateMachine.State {
        /**
         * Synchronize the state machine and hardware.
         */
        SYNC_HARDWARE {
            @Override
            public boolean stateChange() {
                return true;
            }

            /**
             * Reset the encoders to ensure they are at a known good value.
             */
            @Override
            public void execute() {
                me.reset_drive_encoders();
            }
        },
        /**
         * Drive forward until the encoders exceed the specified values.
         */
        DRIVE_FORWARD_1 {
            /**
             * Have the motor shafts turned the required amount?
             *
             * If they haven't, then the op-mode remains in this state (i.e this
             * block will be executed the next time this method is called).
             *
             * @return true if the motor shafts have turned the desired amount, otherwise false
             */
            @Override
            public boolean stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    // Reset the encoders to ensure they are at a known good value.
                    me.reset_drive_encoders();

                    // Stop the motors.
                    me.set_drive_power(0.0f, 0.0f);

                    // Transition to the next state when this method is called
                    // again.
                    return true;
                }

                return false;
            }

            public void execute() {
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                me.run_using_encoders();

                // Start the drive wheel motors at full power.
                me.set_drive_power(1.0f, 1.0f);
            }
        }, WAIT_1 {
            @Override
            public boolean stateChange() {
                return me.have_drive_encoders_reset();
            }

            @Override
            public void execute() {
            }
        },
        /**
         * Turn left until the encoders exceed the specified values.
         */
        TURN_1 {
            @Override
            public boolean stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    me.reset_drive_encoders();
                    me.set_drive_power(0.0f, 0.0f);
                    return true;
                }

                return false;
            }

            @Override
            public void execute() {
                me.run_using_encoders();
                me.set_drive_power(-1.0f, 1.0f);
            }
        },
        /**
         * Wait
         */
        WAIT_2 {
            @Override
            public boolean stateChange() {
                return me.have_drive_encoders_reset();
            }

            @Override
            public void execute() {
            }
        },
        /**
         * Turn right until the encoders exceed the specified values.
         */
        TURN_2 {
            @Override
            public boolean stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    me.reset_drive_encoders();
                    me.set_drive_power(0.0f, 0.0f);
                    return true;
                }

                return false;
            }

            @Override
            public void execute() {
                me.run_using_encoders();
                me.set_drive_power(1.0f, -1.0f);
            }
        },
        /**
         * Wait
         */
        WAIT_3 {
            @Override
            public boolean stateChange() {
                return me.have_drive_encoders_reset();
            }

            @Override
            public void execute() {
            }
        }
    }
}
