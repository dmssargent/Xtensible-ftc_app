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
import org.ftccommunity.ftcxtensible.internal.Alpha;

@Alpha
public class PushBotAutoAdvancedFSM extends PushBotHardware {
    /**
     * This class member remembers which state is currently active.  \
     */
    private FiniteStateMachine2<States, PushBotAutoAdvancedFSM> fsm;

    /**
     * A workaround to access this outer class from the state machine
     */
    private static PushBotAutoAdvancedFSM me;

    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     * <p/>
     * The system calls this member once when the OpMode is enabled.
     */
    @Override
    public void start() {
        // Call the PushBotHardware (super/base class) start method.
        super.start();

        me = this;

        // Reset the motor encoders on the drive wheels.
        reset_drive_encoders();
        fsm = FiniteStateMachine2.create(States.SYNC_HARDWARE, this);
    } // start

    /**
     * Implement a state machine that controls the robot during auto-operation.
     * The state machine uses a class member and encoder input to transition
     * between states.
     * <p/>
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override
    public void loop() {
        fsm.execute();

        // Send telemetry data to the driver station.
        telemetry.addData("18", "StateNode: " + fsm.state().name());

    } // loop

    enum States implements FiniteStateMachine2.State<States> {
        /**
         * Synchronize the state machine and hardware.
         */
        SYNC_HARDWARE {
            @Override
            public States stateChange() {
                return DRIVE_FORWARD_1;
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
            public States stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    // Reset the encoders to ensure they are at a known good value.
                    me.reset_drive_encoders();

                    // Stop the motors.
                    me.set_drive_power(0.0f, 0.0f);

                    // Transition to the next state when this method is called
                    // again.
                    return configureWait(TURN_1);
                }

                return this;
            }

            public void execute() {
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                me.run_using_encoders();

                // Start the drive wheel motors at full power.
                me.set_drive_power(1.0f, 1.0f);
            }
        },
        /**
         * Turn left until the encoders exceed the specified values.
         */
        TURN_1 {
            @Override
            public States stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    me.reset_drive_encoders();
                    me.set_drive_power(0.0f, 0.0f);
                    return configureWait(TURN_2);
                }

                return this;
            }

            @Override
            public void execute() {
                me.run_using_encoders();
                me.set_drive_power(-1.0f, 1.0f);
            }
        },
        /**
         * Turn right until the encoders exceed the specified values.
         */
        TURN_2 {
            @Override
            public States stateChange() {
                if (me.have_drive_encoders_reached(2880, 2880)) {
                    me.reset_drive_encoders();
                    me.set_drive_power(0.0f, 0.0f);

                    return configureWait(DONE);
                }

                return this;
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
        WAIT {
            private States stateAfterwards;

            @Override
            public States stateChange() {
                return me.have_drive_encoders_reset() ? nextStateAfterWait : this;
            }

            @Override
            public void execute() {}
        },
        DONE {
            @Override
            public States stateChange() {
                return this;
            }

            @Override
            public void execute() { /* Do nothing */ }
        }
    }

    private static States nextStateAfterWait;

    /**
     * Configures what the {@link States#WAIT} will return after the wait has finished
     *
     * @param stateAfterWait the state to go to after wait
     * @return the <code>States.WAIT</code> object
     */
    protected static States configureWait(States stateAfterWait) {
        nextStateAfterWait = stateAfterWait;

        return States.WAIT;
    }
}
