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

package org.ftccommunity.ftcxtensible.robot;

import android.util.Log;

import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.RobotContext;

import java.util.logging.Level;

public class RobotLogger {
    private RobotContext context;

    public RobotLogger(RobotContext ctx) {
        context = ctx;
    }

    public void d(String tag, String mess) {
        RobotLog.d(mess);
        context.getStatus().log(Level.FINE, tag, mess);
    }

    public void i(String tag, String mess) {
        RobotLog.i(mess);
        context.getStatus().log(Level.INFO, tag, mess);
    }

    public void w(String tag, String mess) {
        RobotLog.w(mess);
        context.getStatus().log(Level.WARNING, tag, mess);
    }

    public void e(String tag, String mess) {
        RobotLog.e(mess);
        context.getStatus().log(Level.SEVERE, tag, mess);
    }

    public void wtf(String tag, String mess, Exception ex) {
        Log.wtf(tag, mess, ex);
        RobotLog.setGlobalErrorMsg(mess);
    }
}
