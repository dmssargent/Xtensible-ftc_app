package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeInterruptedException;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Provides a means of obtaining thread dumps to long running opmodes, who are not responsive to the
 * kill signals.
 *
 * @author David Sargent
 */
public final class Watchdog2<T extends OpMode> {
    private final static EnumSet<DefaultAsserts> DEFAULT_ASSERTS = EnumSet.allOf(DefaultAsserts.class);
    private final Thread opModeThread;
    private final OpMode opModeToWatch;
    private Thread watchDogThread;
    private boolean interruptIfNecessary = true;
    private boolean armed = false;
    private boolean crashApp = false;
    private List<ConditionalOpModeAssert<T>> conditionalOpModeAssertList;

    public Watchdog2(OpMode opModeToWatch) {
        this(opModeToWatch, Thread.currentThread());
    }

    public Watchdog2(OpMode opModeToWatch, Thread opModeThread) {
        this.opModeToWatch = opModeToWatch;
        this.opModeThread = opModeThread;
        this.conditionalOpModeAssertList = Collections.list((Enumeration<ConditionalOpModeAssert<T>>) DEFAULT_ASSERTS);
    }

    @SafeVarargs
    public final Watchdog2<T> addAssert(ConditionalOpModeAssert<T>... asserts) {
        conditionalOpModeAssertList.addAll(Arrays.asList(asserts));

        return this;
    }

    @Contract(pure = true)
    public boolean willCrashApp() {
        return crashApp;
    }

    public Watchdog2<T> shouldCrashApp(boolean crashApp) {
        this.crashApp = crashApp;

        return this;
    }

    public boolean isInterruptIfNecessary() {
        return interruptIfNecessary;
    }

    public Watchdog2<T> interruptIfNecessary(boolean interruptIfNecessary) {
        this.interruptIfNecessary = interruptIfNecessary;

        return this;
    }

    public boolean isArmed() {
        return armed;
    }

    public void triggerAssert() {
        throwStopAssert();
    }

    public void arm() {
        checkState(!armed || watchDogThread == null || !watchDogThread.isAlive(), "Already armed correctly");
        watchDogThread = new Thread(new Watcher(), opModeToWatch.getClass().getSimpleName() + " Watchdog");
        armed = true;
        watchDogThread.start();

    }

    public void disarm() {
        checkState(armed, "Not armed");
        armed = false;
        watchDogThread.interrupt();
        try {
            watchDogThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    private void throwStopAssert() {
        RobotLog.w("STOP has been requested, however this has not been disarmed");
        final StackTraceElement[] elements = opModeThread.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : elements) {
            stackTrace.append(element.toString()).append('\n');
        }
        RobotLog.w("Long running OpMode stacktrace:\n" + stackTrace.toString());

        if (crashApp) {
            final Activity appContext = (Activity) opModeToWatch.hardwareMap.appContext;
            appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RobotHangException robotHangException = new RobotHangException();
                    robotHangException.setStackTrace(elements);
                    throw robotHangException;
                }
            });
        }
    }

    private enum DefaultAsserts implements ConditionalOpModeAssert {
        LINEAR_OPMODE {
            @Override
            public boolean check(Thread opModeThread, OpMode currentOpMode) {
                return currentOpMode instanceof LinearOpMode &&
                        ((LinearOpMode) currentOpMode).isStopRequested();
            }
        }, THREAD_INTERRUPTS {
            @Override
            public boolean check(Thread opModeThread, OpMode currentOpMode) {
                return opModeThread.isInterrupted();
            }
        }
    }


    public interface ConditionalOpModeAssert<T extends OpMode> {
        boolean check(Thread opModeThread, T currentOpMode);
    }

    private static final class RobotHangException extends RuntimeException {
        private RobotHangException() {
            super("Robot OpMode is no longer responding");
        }
    }

    private class Watcher implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (armed) {
                        for (ConditionalOpModeAssert<T> opModeAssert : conditionalOpModeAssertList) {
                            if (opModeAssert.check(opModeThread, (T) opModeToWatch)) {
                                throwStopAssert();
                            }
                        }
                    }
                    sleep();
                }
            } catch (RuntimeInterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                RobotLog.e("Watchdog has crashed" + ex.toString());
            }
        }
    }
}
