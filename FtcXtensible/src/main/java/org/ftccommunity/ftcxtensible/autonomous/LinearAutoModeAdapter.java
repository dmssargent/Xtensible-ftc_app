/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;

public abstract class LinearAutoModeAdapter extends AutoMode {
    protected LinearAutoModeAdapter() {
        autoState().autonomous().setRunLevels(new SimpleRunLevel[]{
                new SimpleRunLevel() {
                    @Override
                    public int execute() {
                        return init(getContext(), autoState());
                    }
                },
                new SimpleRunLevel() {
                    @Override
                    public int execute() {
                        return run(getContext(), autoState());
                    }
                }, new SimpleRunLevel() {
            @Override
            public int execute() {
                return stop(getContext(), autoState());
            }
        }
        }, 0, 2, 2);
    }

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        autoState().autonomous().runInit();
    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.submitAsyncTask(new Runnable() {
            @Override
            public void run() {
                autoState().autonomous().runFinishLevel();
            }
        });
    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        // todo: add failure processing code
        return -1;
    }

    public abstract int init(RobotContext ctx, AutonomousRobotState state);

    public abstract int run(RobotContext ctx, AutonomousRobotState state);

    public abstract int stop(RobotContext ctx, AutonomousRobotState state);

    public abstract boolean onError(RobotContext ctx, AutonomousRobotState state);

    public abstract int handleError(RobotContext ctx, AutonomousRobotState state, int errorCode);

    public abstract void cleanup(RobotContext ctx, AutonomousRobotState state);

    private abstract class SimpleRunLevel implements RunLevel {
        @Override
        public boolean safe() {
            return onError(getContext(), autoState());
        }

        @Override
        public int safe(int errorlevel) {
            return handleError(getContext(), autoState(), errorlevel);
        }

        @Override
        public void close() {
            cleanup(getContext(), autoState());
        }
    }
}
