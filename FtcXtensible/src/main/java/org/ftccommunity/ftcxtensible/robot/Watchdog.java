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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Watchdog representation for OpModes that allows for synchronisation across multiple OpModes
 *
 * @author David Sargent
 * @since 0.3.1
 */
public class Watchdog {
    private Semaphore lock;
    private Throwable thrownException;
    private boolean locked;

    /**
     * Creates a new Watchdog, with one lock aviabable
     */
    public Watchdog() {
        lock = new Semaphore(1);
        locked = false;
    }

    /**
     * Locks the current Watchdog, if the lock cannot be acquired in 500 milliseconds the attempt to
     * lock fails and the the function returns {@code false}, otherwise {@code true} is returned
     *
     * @return whether the lock was obtained or not
     */
    public synchronized boolean lock() throws InterruptedException {
        boolean lockResult = lock.tryAcquire(500, TimeUnit.MILLISECONDS);
        locked = lockResult || locked;
        synchronized (this) {
            this.notifyAll();
        }
        return lockResult;
    }

    /**
     * Unlocks this Watchdog
     */
    public void unlock() {
        lock.release();
        locked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Gets the current exception that should be processed, if none is present this returns {@code
     * null}
     *
     * @return the current exception or null
     */
    @Nullable
    public Throwable getException() {
        return thrownException;
    }

    /**
     * Records the thrown exception for later processing
     *
     * @param ex the thrown exception to be caught
     * @throws NullPointerException the {@code Throwable} was {@code null}
     */
    public void setException(@NotNull Throwable ex) {
        thrownException = checkNotNull(ex);
    }

    /**
     * Returns true if an exception has been caught and this Watchdog notified of the exception
     *
     * @return if this Watchdog has an exception
     */
    public boolean exceptionCaught() {
        return thrownException != null;
    }
}
