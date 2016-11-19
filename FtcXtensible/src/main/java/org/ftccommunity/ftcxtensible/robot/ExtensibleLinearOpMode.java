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

import com.google.common.base.Throwables;
import com.google.common.collect.EvictingQueue;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeInterruptedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An extensible version of a linear OpMode, designed for higher levels of synchronisation as well
 * as better access to the underlying loop. This locks/unlocks a {@link Watchdog} object to a allow
 * synchronisation, if need be. This notifies all threads waiting on this object when a new loop
 * cycle is being executed. An {@link org.ftccommunity.ftcxtensible.robot.ExtensibleLinearOpMode.EventWatcher}
 * can be registered on this to allow for the receiving of Event notifications.
 *
 * @author David Sargent
 * @since 0.3.1
 */
public abstract class ExtensibleLinearOpMode extends ExtensibleOpMode {
    private final EvictingQueue<Object> queue;
    private final Thread linearThread;
    private final Watchdog watchdog;
    private EventWatcher eventWatcher;
    private OpModeState state;

    protected ExtensibleLinearOpMode() {
        watchdog = new Watchdog();
        linearThread = new Thread(new LinearRunner());
        queue = EvictingQueue.create(50);
    }

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        watchdog().unlock();
        changeState(OpModeState.INIT);


        linearThread.setUncaughtExceptionHandler(new LinearThreadExceptionHandler(watchdog()));
        linearThread.setPriority(8);
        linearThread.setName("OpMode Runner");

        linearThread.start();
        watchdog().lock();
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        _beginLoop();
        changeState(OpModeState.INIT_LOOP);

        watchdog().lock();
    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {
        _beginLoop();
        changeState(OpModeState.START);

        watchdog().lock();
    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        _beginLoop();
        changeState(OpModeState.LOOP);
        synchronized (this) {
            this.notify();
        }

        watchdog().lock();
    }


    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        changeState(OpModeState.STOP);
        watchdog().lock();
        linearThread.interrupt();

        Thread.sleep(25);
        if (linearThread.isAlive()) {
            throw new IllegalStateException("Long running linear opmode");
        }

        watchdog().unlock();
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {
        if (in != null) {
            queue().add(in);
        }

        if (getEventWatcher() != null) {
            getEventWatcher().success(event);
        }
    }

    /**
     * The entry-point for user code execution. All code inside here gets executed by a high-
     * priority thread named "OpMode Runner." Call on the {@link ExtensibleLinearOpMode#watchdog()}
     * to sync up with the looping structures within the OpMode.
     */
    protected abstract void runOpMode() throws Exception;

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        if (in != null) {
            queue().add(in);
        }

        if (getEventWatcher() != null) {
            getEventWatcher().failure(event);
        }

        return -1;
    }

    /**
     * Checks for exceptions caught by the Watchdog via the registered Unhandled Exception Handler,
     * as well as, unlocking the Watchdog.
     */
    private void _beginLoop() {
        watchdog().unlock();

        if (watchdog().exceptionCaught()) {
            Throwable exception = watchdog().getException();
            if (exception != null) {
                Throwables.propagate(exception);
            }
        }
    }

    /**
     * Returns the current OpMode state
     *
     * @return the OpMode state
     * @see org.ftccommunity.ftcxtensible.robot.ExtensibleLinearOpMode.OpModeState
     */
    @NotNull
    @Contract(pure = true)
    protected final OpModeState state() {
        return state;
    }

    /**
     * Blocks until a given OpMode state is achevied
     *
     * @param waitTillState the state to block until the OpMode achieves
     * @throws InterruptedException     the current thread gets interrupted while waiting on the
     *                                  state change
     * @throws IllegalArgumentException the state has already been achieved in the OpMode
     */
    protected final void waitUntil(@NotNull OpModeState waitTillState) throws
            InterruptedException, IllegalArgumentException {
        checkArgument(waitTillState.ordinal() <= state.ordinal(),
                "The given state has already been achieved");

        while (state != checkNotNull(waitTillState)) {
            synchronized (state) {
                state.wait();
            }
        }
    }

    /**
     * Waits for the OpMode state to achieve a the {@code OpModeState.START} state
     *
     * @throws InterruptedException  the thread is interrupted while waiting for the state change
     * @throws IllegalStateException the {@code START} changeover has already been achieved
     */
    protected final void waitForStart() throws InterruptedException, IllegalStateException {
        try {
            waitUntil(OpModeState.START);
        } catch (IllegalArgumentException ex) {
            IllegalStateException illegalStateException = new IllegalStateException("START has already been achieved");
            illegalStateException.addSuppressed(ex);
            throw illegalStateException;
        }
    }

    /**
     * Changes the current OpMode state to the given state as well as notifiying all objects waiting
     * on the current state about the change
     *
     * @param newState the new OpMode state
     */
    private void changeState(OpModeState newState) {
        state = newState;
        synchronized (this) {
            synchronized (state) {
                state.notifyAll();
            }
        }
    }

    /**
     * Sleeps the calling thread for the given number of milliseconds
     *
     * @param milliseconds the number of milliseconds to sleep
     * @throws RuntimeInterruptedException the calling thread was interrupted while sleeping
     */
    protected void sleep(long milliseconds) throws RuntimeInterruptedException {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            throw new RuntimeInterruptedException(ex);
        }
    }

    /**
     * Sleeps the calling thread for the given duration. The duration is then recalculated into
     * milliseconds in a process that truncates the given duration, for example if this was called
     * with 45 for the duration and {@code NANOSECONDS} for the unit, the current thread
     * would sleep for 0 milliseconds.
     *
     * @param duration the number of units to sleep for
     * @param unit     what unit the duration is in
     * @throws RuntimeInterruptedException the calling thread was interrupted while sleeping
     */
    protected final void sleep(long duration, TimeUnit unit) throws RuntimeInterruptedException {
        sleep(TimeUnit.MILLISECONDS.convert(duration, unit));
    }

    /**
     * Blocks until the given number of loop cycles has happened
     *
     * @param loops the number of loops to block for
     * @throws InterruptedException     the calling thread has been interrupted
     * @throws IllegalArgumentException the loops parameter is negative or equal to zero
     */
    protected void waitForLoops(int loops) throws InterruptedException, IllegalArgumentException {
        checkArgument(loops > 0, "The loops parameter may not be less than or equal to zero.");
        int start = getLoopCount();
        while (getLoopCount() < start + loops) {
            synchronized (this) {
                this.wait();
            }
        }
    }

    /**
     * Waits for one loop cycle to happen. This returns at the very next loop after the original
     * calling one loop  has returned back to the SDK. If you need more please use {@link
     * ExtensibleLinearOpMode#waitForLoops(int)}
     *
     * @throws InterruptedException the calling thread was interrupted while sleeping
     */
    protected void waitOneLoop() throws InterruptedException {
        waitForLoops(1);
    }

    /**
     * Returns the current object queue. This queue will only store up to 50 objects, afterwards the
     * oldest object will be removed, and the new object inserted.
     *
     * @return an {@link EvictingQueue<Object>} that stores elements
     */
    @NotNull
    public EvictingQueue<Object> queue() {
        return queue;
    }

    /**
     * Returns the {@link org.ftccommunity.ftcxtensible.robot.ExtensibleLinearOpMode.EventWatcher}
     * for the current OpMode executing
     *
     * @return the current {@code EventWatcher} for this OpMode
     */
    public EventWatcher getEventWatcher() {
        return eventWatcher;
    }

    /**
     * Sets a {@link org.ftccommunity.ftcxtensible.robot.ExtensibleLinearOpMode.EventWatcher} to
     * monitor events generated by the current OpMode, this replaces any given {@code EventWatcher}
     * already set.
     *
     * @param watcher a new Event Watcher
     * @throws NullPointerException the watcher is null
     */
    protected void setEventWatcher(@NotNull EventWatcher watcher) {
        eventWatcher = checkNotNull(watcher);
    }

    /**
     * Returns the current OpMode {@link Watchdog}
     *
     * @return the current {@code Watchdog} for the given OpMode
     */
    @NotNull
    public Watchdog watchdog() {
        return watchdog;
    }

    /**
     * The available OpMode running states
     *
     * @author David Sargent
     * @since 0.3.1
     */
    public enum OpModeState {
        INIT, INIT_LOOP, START, LOOP, STOP
    }

    /**
     * The Event Watcher for the Linear OpMode execution system. Various events generated by the
     * Xtensible SDK will be forwarded to user supplied definitions of the event handler
     *
     * @author David Sargent
     * @since 0.3.1
     */
    protected interface EventWatcher {
        /**
         * Handles an OpMode failure, the given event is somehow related to that OpMode failure
         *
         * @param event the failure event of, or relating to, the current OpMode
         */
        void failure(Object event);

        /**
         * Handles an OpMode success event of various types
         *
         * @param event an even representing a success type
         */
        @Deprecated
        void success(Object event);
    }

    /**
     * The Runnable interface for the Linear OpMode thread.
     *
     * @author David Sargent
     * @since 0.3.1
     */
    private class LinearRunner implements Runnable {

        /**
         * Starts executing the active part of the class' code. This method is called when a thread
         * is started that has been created with a class which implements {@code Runnable}.
         */
        @Override
        public void run() {
            try {
                runOpMode();
            } catch (InterruptedException ex) {
                linearThread.interrupt();
            } catch (Exception e) {
                watchdog().setException(e);
            }
        }
    }

    /**
     * The LinearOpMode Unhandled Exception Handler for the LinearOpMode
     *
     * @author David Sargent
     * @since 0.3.1
     */
    private class LinearThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Watchdog watchdog;

        public LinearThreadExceptionHandler(Watchdog watcher) {
            watchdog = watcher;
        }

        /**
         * The thread is being terminated by an uncaught exception. Further exceptions thrown in
         * this method are prevent the remainder of the method from executing, but are otherwise
         * ignored.
         *
         * @param thread the thread that has an uncaught exception
         * @param ex     the exception that was thrown
         */
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                Throwables.propagateIfInstanceOf(ex, InterruptedException.class);
                watchdog.setException(ex);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
