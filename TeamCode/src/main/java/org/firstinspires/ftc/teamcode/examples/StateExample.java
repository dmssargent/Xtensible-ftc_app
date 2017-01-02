package org.firstinspires.ftc.teamcode.examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.EnumSet;

import static org.firstinspires.ftc.teamcode.examples.StateExample.AutoStates.STATE1;

@Autonomous
@Disabled
public class StateExample extends OpMode {
    private State currState;

    @Override
    public void init() {
        final EnumSet<AutoStates> autoStates = EnumSet.allOf(AutoStates.class);
        for (AutoStates state : autoStates) {
            state.opMode = this;
        }
        currState = STATE1;
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {
        // Check if the state machine has finished
        if (currState.nextState() == null) return;

        if (currState.nextState() != currState) {
            currState = currState.nextState();
            currState.init();
        }

        currState.perform();
        telemetry.addData("STATE", currState.toString());
    }

    enum AutoStates implements State {
        STATE1 {
            // we need to set a value to this, in order for nextState to work before init() is called
            private State nextState = this;

            @Override
            public void init() { // Maybe called more than once needs to reset state
                nextState = this;
                // todo: init code here, should reset state
            }

            @Override
            public void perform() {
                // todo: what you want this state to perform
            }

            @Override
            public State nextState() {
                return nextState;
            }
        }, STATE2 {
            @Override
            public void init() { // Maybe called more than once needs to reset state
                // todo: init code here, should reset state
            }

            @Override
            public void perform() {
                // todo: what you want this state to perform
            }

            @Override
            public State nextState() {
                return null;
            }
        };

        OpMode opMode;
    }

    interface State {
        void init();

        void perform();

        State nextState();
    }
}
