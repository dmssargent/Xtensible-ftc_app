package org.ftccommunity.ftcxtensible.collections;


import android.util.Log;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeInterruptedException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SynchronousArrayQueue<T> extends ArrayQueue<T> {
    private static final String TAG = SynchronousArrayQueue.class.toString();
    private final Semaphore lock = new Semaphore(1, true);

    public SynchronousArrayQueue(int size) {
        super(size);
    }

    public SynchronousArrayQueue() {
        super();
    }

    public void close() {
        if (lock("close")) {
            super.close();
            unlock();
            return;
        }

        throw new IllegalStateException("Couldn't acquire lock in a timely manner");
    }

    private boolean lock(String name) {
        try {
            Log.d(TAG, Thread.currentThread().getName() + " is trying to acquire a lock. id=" + name);
            return lock.tryAcquire(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeInterruptedException(e);
        }
    }

    private void unlock() {
        Log.d(TAG, Thread.currentThread().getName() + " is releasing a lock.");
        lock.release();
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public int length() {
        if (lock("length")) {
            int length = super.length();
            unlock();
            return length;
        }

        throw new IllegalStateException("Couldn't acquire lock in a timely manner");
    }

    public void add(T element) {
        if (lock("add")) {
            super.add(element);
            unlock();
            return;
        }

        throw new IllegalStateException("Couldn't acquire lock in a timely manner");
    }

    public T remove() {
        if (lock("remove")) {
            T element = super.remove();
            unlock();
            return element;
        }

        throw new IllegalStateException("Couldn't acquire lock in a timely manner");
    }
}
