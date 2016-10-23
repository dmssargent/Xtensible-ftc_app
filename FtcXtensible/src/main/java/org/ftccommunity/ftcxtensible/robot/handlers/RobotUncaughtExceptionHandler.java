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
package org.ftccommunity.ftcxtensible.robot.handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Throwables;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

public class RobotUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final int delay;
    public Context context;
    private PendingIntent pendingIntent;

    public RobotUncaughtExceptionHandler(Context ctx, PendingIntent intent, int delay) {
        context = ctx;
        pendingIntent = intent;
        this.delay = delay;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        try {
            Log.wtf("CORE_CONTROLLER::", Throwables.getRootCause(exception));
            Log.i("CORE_CONTROLLER::", "Exception Details:", exception);
            try {
                Toast.makeText(context,
                        "An almost fatal exception occurred." + exception.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Log.i(ExtensibleOpMode.TAG, ex.getLocalizedMessage());
            }

            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delay, pendingIntent);
            System.exit(2);
        } catch (Exception e) {
            Log.wtf("CORE_CONTROLLER::", e);
        }
    }
}
