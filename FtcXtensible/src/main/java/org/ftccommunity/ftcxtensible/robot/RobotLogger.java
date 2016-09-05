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

package org.ftccommunity.ftcxtensible.robot;

import android.util.Log;

import com.qualcomm.robotcore.util.RobotLog;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A Logger that writes to both the Xtensible Robot Status Log, and the Android logcat
 *
 * @since 0.1.0
 * @author David Sargent
 */
public final class RobotLogger {
    private static RobotLogger logger;
    private final RobotContext context;

    private RobotLogger(RobotContext ctx) {
        context = ctx;
    }

    /**
     * Gets the current instance of this logger. Must have called
     * {@link #createInstance(RobotContext)} prior to using this function
     *
     * @return the current instance of {@code RobotLogger}
     */
    public static RobotLogger getInstance() throws IllegalStateException {
        checkState(logger != null, "RobotLogger has not been configured yet");

        return logger;
    }

    /**
     * Creates a new {@link RobotLogger}, and returns the new instance. Calling this allows
     * {@link #getInstance()} to work
     *
     * @param ctx a non-null {@link RobotContext} to use in this Logger
     * @return a new RobotLogger
     * @throws NullPointerException if the RobotContext is null
     */
    public static synchronized RobotLogger createInstance(@NotNull final RobotContext ctx) throws NullPointerException {
        logger = new RobotLogger(checkNotNull(ctx));
        return logger;
    }

    /**
     * Checks if this logger is ready to be used
     *
     * @return {@code true} if this logger is ready to use, otherwise {@code false}
     */
    public static boolean isValid() {
        try {
            return logger != null && logger.context.status() != null;
        } catch (NullPointerException ex) {
            return false;
        } catch (Exception ex) {
            Log.wtf("ROBOT_LOGGER", ex);
            return false;
        }
    }

    /**
     * Writes a debug message to the respective loggers.
     *
     * @param tag the log tag
     * @param mess the message to send
     * @see RobotLog#d(String)
     * @see RobotStatus#log(Level, String, String)
     * @see Level#FINE
     */
    public synchronized static void d(@NotNull String tag, @NotNull String mess) {
        tag = checkNotNull(tag);
        mess = checkNotNull(mess);
        RobotLog.d(mess);
        getInstance().context.status().log(Level.FINE, tag, mess);
    }

    /**
     * Writes a info message to the respective loggers.
     *
     * @param tag the log tag
     * @param mess the message to send
     * @see RobotLog#i(String)
     * @see RobotStatus#log(Level, String, String)
     * @see Level#INFO
     */
    public synchronized static void i(String tag, String mess) {
        tag = checkNotNull(tag);
        mess = checkNotNull(mess);

        RobotLog.i(mess);
        getInstance().context.status().log(Level.INFO, tag, mess);
    }

    /**
     * Writes a warning message to the respective loggers.
     *
     * @param tag the log tag
     * @param mess the message to send
     * @see RobotLog#w(String)
     * @see RobotStatus#log(Level, String, String)
     * @see Level#WARNING
     */
    public synchronized static void w(String tag, String mess) {
        tag = checkNotNull(tag);
        mess = checkNotNull(mess);

        RobotLog.w(mess);
        getInstance().context.status().log(Level.WARNING, tag, mess);


    }

    /**
     * Writes a error message to the respective loggers.
     *
     * @param tag the log tag
     * @param mess the message to send
     * @see RobotLog#e(String)
     * @see RobotStatus#log(Level, String, String)
     * @see Level#SEVERE
     */
    public synchronized static void e(String tag, String mess) {
        tag = checkNotNull(tag);
        mess = checkNotNull(mess);

        RobotLog.e(mess);
        getInstance().context.status().log(Level.SEVERE, tag, mess);
    }

    /**
     * Writes a debug message to the respective loggers.
     *
     * @param tag the log tag
     * @param mess the message to send
     * @see RobotLog#setGlobalErrorMsg(String)
     * @see Log#wtf(String, String)
     */
    public synchronized static void wtf(String tag, String mess, Exception ex) {
        tag = checkNotNull(tag);
        mess = checkNotNull(mess);
        ex = checkNotNull(ex);

        Log.wtf(tag, mess, ex);
        RobotLog.setGlobalErrorMsg(mess);
    }
}
