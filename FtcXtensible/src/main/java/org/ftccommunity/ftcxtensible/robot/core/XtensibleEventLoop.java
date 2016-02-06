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
package org.ftccommunity.ftcxtensible.robot.core;

import android.content.Context;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Command;

import java.util.concurrent.Semaphore;

/**
 * Override for {@link FtcEventLoop}. This fixes some issues present in the standard OpMode
 *
 * @since 0.3.0
 * @version 1
 * @author David Sargent
 * @author Bob Atkinson
 */
public class XtensibleEventLoop extends FtcEventLoop {
    private Semaphore semaphore;

    public XtensibleEventLoop(HardwareFactory hardwareFactory, OpModeRegister register,
                              UpdateUI.Callback callback, Context robotControllerContext) {
        super(hardwareFactory, register, callback, robotControllerContext);
        semaphore = new Semaphore(0);
    }

    @Override
    public void init(EventLoopManager eventLoopManager) throws
            RobotCoreException, InterruptedException {
        super.init(eventLoopManager);
        semaphore.release();
    }

    @Override
    public void processCommand(Command command) {
        try {
            semaphore.acquire();
            super.processCommand(command);
            semaphore.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
