package org.ftccommunity.ftcxtensible.robot.handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Throwables;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

public class RobotUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static Context context;
    PendingIntent pendingIntent;

    public RobotUncaughtExceptionHandler(Context ctx, PendingIntent intent) {
        context = ctx;
        pendingIntent = intent;
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
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
            System.exit(2);
        } catch (Exception e) {
            Log.wtf("CORE_CONTROLLER::", e);
        }
    }
}
