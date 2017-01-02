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
package org.firstinspires.ftc.teamcode.statemachine;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.jetbrains.annotations.Contract;

@Autonomous
public class ExampleOpMode extends OpMode {
    AdvancedFiniteStateMachine<States, ExampleOpMode> finiteStateMachine;

    @Override
    public void init() {
        finiteStateMachine = new AdvancedFiniteStateMachine<>(States.INIT, this);
    }

    @Override
    public void loop() {
        finiteStateMachine.execute();
    }

    enum States implements AdvancedFiniteStateMachine.State, AdvancedFiniteStateMachine.StateEnum {
        INIT {
            private Telemetry telemetry;

            @Override
            public int stateChange() {
                waitFor();
                return AdvancedFiniteStateMachine.NEXT;
            }

            @Override
            public void execute() {
                telemetry.addData("HI", "I am " + this.name());
            }
        }, LOOP {
            private Telemetry telemetry;

            @Override
            public int stateChange() {
                return AdvancedFiniteStateMachine.REMAIN;
            }

            @Override
            public void execute() {
                telemetry.addData("HI", "I am done. " + this.name());
            }
        };

        private final int WAIT = -1;
        private final int NEXT = -2;
        private final int REMAIN = -3;
        private long waitTime;

        public static int valueOf(States state) {
            return state.ordinal();
        }

        public void waitFor(long time) {
            if (time < 0) {
                throw new IllegalArgumentException();
            }
            waitTime = time;
        }

        public long waitFor() {
            return waitTime;
        }

        @Contract(pure = true)
        public States waitState() {
            return LOOP;
        }
    }
}


