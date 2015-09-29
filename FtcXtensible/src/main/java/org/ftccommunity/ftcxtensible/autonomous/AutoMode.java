/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public abstract class AutoMode extends OpMode {
    public Autonomous auto;
    public RobotState state;
    private Thread autoThread;

    public AutoMode() {
        autoThread = new Thread(auto);
    }

    public AutoMode(Autonomous newAuto) {
        state = new RobotState();
        auto = newAuto;
        autoThread = new Thread(auto);
    }

    public void init() {
        auto.RunInitLevel();
    }

    public void run() {
        if (!autoThread.isAlive() && !auto.isDone()) {
            autoThread.start();
        }
        state.SyncState();
    }

    public void done() {
        auto.RunFinishLevel();
        auto.close();
    }


}
