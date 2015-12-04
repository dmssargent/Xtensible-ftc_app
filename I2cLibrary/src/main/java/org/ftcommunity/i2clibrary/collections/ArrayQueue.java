/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftcommunity.i2clibrary.collections;

/**
 * A collection that is a high-performance array backed queue
 *
 * @param <AnyType> the type of element to store inside the queue
 * @author Olavi Kamppari
 * @since 0.3.1
 */
public class ArrayQueue<AnyType> {
    private static final int DEFAULT_SIZE = 4;
    private AnyType[] queue;
    private int queueSize;
    private int head;
    private int tail;

    public ArrayQueue() {
        queue = newQueue(DEFAULT_SIZE);
        queueSize = queue.length;
        head = 0;
        tail = 0;
    }

    @SuppressWarnings("unchecked") // The array casting is OK
    private AnyType[] newQueue(int size) throws ClassCastException {
        return (AnyType[]) new Object[size];
    }

    public void close() {
        while (!isEmpty()) {
            remove(); // Discard all elements
        }
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public int length() {
        return (tail + queueSize - head) % queueSize;
    }

    public void add(AnyType element) {
        int nextTail = (tail + 1) % queueSize;
        if (nextTail == head) { // The queue is full
            int i;              // Double the queue size
            AnyType[] nextQueue = newQueue(2 * queueSize);
            // Copy the elements from the original queue
            for (i = 0; head != tail; i++, head = (head + 1) % queueSize) {
                nextQueue[i] = queue[head];
                queue[head] = null;         // Support garbage collection
            }
            queue = nextQueue;
            queueSize = queue.length;
            head = 0;
            tail = i;
            nextTail = (tail + 1) % queueSize;
        }

        queue[tail] = element;
        tail = nextTail;
    }

    public AnyType remove() {
        if (isEmpty()) {
            return null;
        }

        AnyType element = queue[head];
        queue[head] = null;  // Enable garbage collection
        head = (head + 1) % queueSize;
        return element;
    }
}
