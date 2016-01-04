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
package org.ftcommunity.i2clibrary;

import org.ftcommunity.i2clibrary.interfaces.IHandshakeable;

/**
 * A class that helps us start a thread and interlock with its actual starting up.
 * <p>
 * It's surprisingly often that in order to be able to correctly shut down a thread after
 * it has begun, or to give the thread the opportunity to acquire resources it needs to operate,
 * that one shouldn't return from the logic that 'starts' the thread until the thread has
 * begun execution and positively indicated that it's good to go.
 * <p>
 * This class helps to implement that handshake logic.
 * <p>
 * That all said, this class is probably less useful than we initially thought it would be.
 * And its implementation should be cleaned up to use ExecutorService's.
 */
public class HandshakeThreadStarter {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    private final Object eventLock = new Object();
    private String name;
    private IHandshakeable shakeable;
    private boolean eventSignalled = false;
    private Thread thread = null;
    private volatile boolean started = false;
    private volatile boolean stopRequested = false;

    public HandshakeThreadStarter(String name, IHandshakeable shakeable) {
        this.shakeable = shakeable;
        this.name = name;
    }

    public HandshakeThreadStarter(IHandshakeable shakeable) {
        this(null, shakeable);
    }

    public String getName() {
        return this.name;
    }

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public void setName(String name) {
        this.name = name;
    }

    public Thread getThread() {
        return this.thread;
    }

    //----------------------------------------------------------------------------------------------
    // Public methods
    //----------------------------------------------------------------------------------------------

    /**
     * Get everything ready for starting
     */
    public synchronized Thread prepareStart() {
        stop();

        resetEvent();
        this.stopRequested = false;
        this.thread = new Thread(new Runner());
        if (this.name != null)
            this.thread.setName(this.name);

        return this.thread;
    }

    /**
     * Starts the thread going. Blocks until the thread actually runs and calls starter.handshake().
     */
    public synchronized void start() {
        try {
            if (this.thread == null)
                prepareStart();

            this.thread.start();
            waitEvent();

            this.started = true;
        } catch (Exception e) {
            // Clean up if we were interrupted while waiting
            this.started = true;    // so stop() will work
            stop();

            Util.handleCapturedException(e);
        }
    }

    /**
     * Returns whether the thread is, for the moment, started
     */
    public synchronized boolean isStarted() {
        return this.started;
    }

    /**
     * Requests that the thread stop at its earliest available opportunity
     */
    public synchronized void requestStop() {
        this.stopRequested = true;
        if (this.thread != null)
            this.thread.interrupt();
    }

    /**
     * Stops the thread, if currently running. Blocks until thread terminates or for msWait ms.
     */
    public synchronized void stop(int msWait) {
        if (this.started) {
            try {
                this.requestStop();
                if (msWait == 0)
                    this.thread.join();
                else
                    this.thread.join(msWait);
            } catch (Exception e) {
                Util.handleCapturedException(e);
            } finally {
                this.thread = null;
                this.started = false;
            }
        }
    }

    /**
     * Stops the thread, if currently running. Blocks until thread terminates.
     */
    public void stop() {
        stop(0);
    }

    /**
     * block until the thread terminates. Note that this does not itself stop the thread
     */
    public void join() throws InterruptedException {
        Thread thread;
        synchronized (this) {
            thread = this.thread;
        }
        if (thread != null)
            thread.join();
    }

    public void join(int ms) throws InterruptedException {
        Thread thread;
        synchronized (this) {
            thread = this.thread;
        }
        if (thread != null)
            thread.join(ms);
    }
    //----------------------------------------------------------------------------------------------
    // For use by thread
    //----------------------------------------------------------------------------------------------

    /**
     * called by the thread to indicate that he's alive and well
     */
    public void doHandshake() {
        this.setEvent();
    }

    /**
     * Returns whether the thread has been asked to stop
     */
    public synchronized boolean isStopRequested() {
        return this.stopRequested || (this.thread != null && this.thread.isInterrupted());
    }

    //----------------------------------------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------------------------------------

    /* make it so that subsequent waiters will block */
    void resetEvent() {
        synchronized (this.eventLock) {
            this.eventSignalled = false;
        }
    }

    /* wait until setEvent() has happened. leave state unchanged as a result of our waiting */
    void waitEvent() throws InterruptedException {
        synchronized (this.eventLock) {
            while (!this.eventSignalled) {
                this.eventLock.wait();
            }
        }
    }

    /* make it so that subsequent waiters will not block. release anyone currently waiting  */
    void setEvent() {
        synchronized (this.eventLock) {
            this.eventSignalled = true;
            this.eventLock.notifyAll();
        }
    }

    class Runner implements Runnable {
        @Override
        public void run() {
            shakeable.run(HandshakeThreadStarter.this);
        }
    }
}

