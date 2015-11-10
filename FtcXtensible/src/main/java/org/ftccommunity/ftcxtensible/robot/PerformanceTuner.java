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

package org.ftccommunity.ftcxtensible.robot;

import java.io.Serializable;

/**
 * A serializable class that describes key info in how a loop performs in operation
 *
 * @author David Sargent
 * @since 0.3.1
 */
public class PerformanceTuner implements Serializable {
    private static final long serialVersionUID = 0l;
    private Integer[] times;
    private double[] z_indexes;
    private double average;
    private double stdDev;

    public PerformanceTuner(Integer[] times) {
        this.times = times;
        average = 0;
        for (int time :
                times) {
            average = getAverage() + time;
        }
        average = getAverage() / times.length;

        stdDev = 0;
        for (int time :
                times) {
            stdDev += Math.pow(time - getAverage(), 2);
        }
        stdDev = getStdDev() / times.length;

        z_indexes = new double[times.length];
        for (int i = 0; i < times.length; i++) {
            z_indexes[i] = (times[i] - getAverage()) / stdDev;
        }
    }

    public Integer[] getTimes() {
        return times;
    }

    public double[] getZ_indexes() {
        return z_indexes;
    }

    public double getAverage() {
        return average;
    }

    public double getStdDev() {
        return stdDev;
    }
}
