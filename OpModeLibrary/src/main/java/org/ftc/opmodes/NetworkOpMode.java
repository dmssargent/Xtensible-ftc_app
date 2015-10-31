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


import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;

import java.util.LinkedList;
import java.util.logging.Level;

@RobotSdkVersion(RobotSdkApiLevel.R3_2015)
@Autonomous(name = "Network Op")
public class NetworkOpMode extends ExtensibleOpMode {
    private int loopCount;

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.enableNetworking().startNetworking();
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {
        loopCount = 0;
    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        loopCount++;
        if (loopCount % 10 == 0) {
            ctx.status().log(Level.WARNING, "FOO", "This is opMode test! " + loopCount++);
        }
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.stopNetworking().disableNetworking();
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }


}
