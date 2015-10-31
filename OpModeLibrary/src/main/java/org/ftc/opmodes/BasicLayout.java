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

package org.ftc.opmodes;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;

public class BasicLayout extends ExtensibleOpMode {
    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) {

    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    /**
     * Something went wrong? This is where you process it.
     *
     * @param ctx the Robot context
     * @param eventType the type of event
     * @param event the actual event
     * @param in a failure event
     * @return an integer value representing what to do; values < 0 stops the robot
     */
    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }
}
