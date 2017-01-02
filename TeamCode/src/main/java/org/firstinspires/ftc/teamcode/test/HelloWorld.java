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

package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.ftccommunity.ftcxtensible.opmodes.RobotsDontQuit;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotLogger;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.Date;
import java.util.LinkedList;

@Autonomous(name = "Hello World Example")
@RobotsDontQuit
@Disabled
public class HelloWorld extends SimpleOpMode {
    private final String MESS = "MESS";
    private volatile boolean test = false;

    @Override
    public void init(RobotContext ctx) {
        enableNetworking().startNetworking();
    }

    @Override
    public void loop(RobotContext ctx) {
        telemetry.data(MESS, "Hello, World!");
        telemetry.data(MESS, "How are you doing?");
        telemetry.data("TEST", test);

    }

    @Override
    public void start(RobotContext ctx) throws Exception {
        telemetry.data(MESS, "Start Date: " +
                (new Date((long) (System.nanoTime() / 1E3))).toString());
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        RobotLogger.w(MESS, "End Date: " +
                (new Date(System.nanoTime() / 1000)).toString() + ". This ran for " + getRuntime());
    }
}
