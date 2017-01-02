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

import com.google.common.collect.EvictingQueue;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Autonomous
@Disabled
public class PerformanceTest extends SimpleOpMode {
    private static final int REDUCE_BY_FACTOR = 50;
    private final static String test = "TEST";
    private final static String warmUp = "Warming up...";
    private final static String performTest = "Performing test...";
    private long lastTime;
    private Queue<Long> timeQueue = EvictingQueue.create(1000000 / REDUCE_BY_FACTOR);
    private String result;

    private static long convert(long nano) {
        return TimeUnit.NANOSECONDS.toMillis(nano);
    }

    @Override
    public void init(RobotContext ctx) throws Exception {

    }

    @Override
    public void start(RobotContext ctx) {
        lastTime = System.nanoTime();
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        final long delta = System.nanoTime() - lastTime;
        if (getLoopCount() < 20000 / REDUCE_BY_FACTOR) {
            timeQueue.add(delta);
            telemetry.data(test, warmUp);
        } else if (getLoopCount() >= 20000 / REDUCE_BY_FACTOR && getLoopCount() < 1.02E6 / REDUCE_BY_FACTOR) {
            timeQueue.add(delta);
            telemetry.data(test, performTest);
        }

        if (getLoopCount() > 1.2E6 / REDUCE_BY_FACTOR) {
            if (result == null) {
                List<Long> list = Arrays.asList(timeQueue.toArray(new Long[timeQueue.size()]));
                Collections.sort(list);
                long sumOfDifferences = 0;
                for (int i = 0; i < list.size(); i++) {
                    sumOfDifferences += list.get(i);
                }

                result = "min: " + convert(list.get(0)) + "; max: " + convert(list.get(list.size() - 1)) + "; avg: " + convert(sumOfDifferences / list.size());
            }
            telemetry.data(test, result);
        }

        lastTime = System.nanoTime();
    }
}
