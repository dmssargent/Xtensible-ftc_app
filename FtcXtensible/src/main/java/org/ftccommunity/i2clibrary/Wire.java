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

package org.ftccommunity.i2clibrary;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.ftccommunity.i2clibrary.collections.ArrayQueue;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;

/**
 * A generic Ardunio-like interface for using I2C compatible devices within the Robot Controller
 *
 * @author Olavi Kamppari
 * @since 0.3.1
 */
public class Wire implements I2cController.I2cPortReadyCallback {
// --------------------------------- CONSTANTS -------------------------------------------------

    // Cache buffer constant values
    static final byte
            /**
             * MSB 1
             */
            READ_MODE = 0x00 - 128,   // MSB = 1
    /**
     * MSB 0
     */
    WRITE_MODE = 0x00;         // MSB = 0

    // Cache buffer index values
    static final int
            CACHE_MODE = 0,            // MSB = 1 when read mode is active
            DEV_ADDR = 1,            // Device address
            REG_NUMBER = 2,            // Register address
            REG_COUNT = 3,            // Register count
            DATA_OFFSET = 4,            // First byte of transferred data
            LAST_INDEX = 29,           // Last index available for data
            ACTION_FLAG = 31,           // 0 = idle, -1 = transfer is active
            CACHE_SIZE = 32;           // downStreamCache fixed size

    // --------------------------------- CLASS VARIABLES -------------------------------------------
    private ArrayQueue<Element> downQueue;  // Down stream buffer
    private ArrayQueue<Element> upQueue;    // Up stream buffer
    private I2cDevice wireDevice;              // Generic I2C Device Object
    private byte wireDevAddress;               // Generic Device Address
    private byte[] readCache;                  // READ Cache
    private byte[] writeCache;                  // WRITE Cache
    private Lock readLock;                     // Lock for READ Cache
    private Lock writeLock;                     // Lock for WRITE Cache & request queue

    private byte[] downStreamCache;                  // Buffer for down stream details
    private int downstreamNextLocation;                      // Next location for incoming bytes
    private byte[] upstreamCache;                  // Buffer for up stream response
    private int upstreamNextCache;                      // Next location for response bytes
    private int uLimit;                     // Last location for response bytes
    private long uMicros;                   // Time stamp, microseconds since start
    private long startTime;                 // Start time in nanoseconds
    private boolean isIdle;                 // Mechanism to control polling

// --------------------------------- CLASS INIT AND CLOSE ---------------------------------------

    private Wire() {
        downQueue = new ArrayQueue<>();
        upQueue = new ArrayQueue<>();

        downStreamCache = new byte[CACHE_SIZE];
        downstreamNextLocation = DATA_OFFSET;
        upstreamCache = new byte[CACHE_SIZE];
        uMicros = 0L;
        startTime = System.nanoTime();
        upstreamNextCache = DATA_OFFSET;
        uLimit = upstreamNextCache;
        isIdle = true;
    }

    public Wire(@NotNull I2cDevice device, int deviceAddress) {
        this();
        this.wireDevice = device;
        wireDevAddress = (byte) deviceAddress;

        readCache = wireDevice.getI2cReadCache();
        writeCache = wireDevice.getI2cWriteCache();
        readLock = wireDevice.getI2cReadCacheLock();
        writeLock = wireDevice.getI2cWriteCacheLock();

        wireDevice.registerForI2cPortReadyCallback(this);
    }

    public Wire(HardwareMap hardwareMap, String deviceName, int devAddr) {
        this(hardwareMap.i2cDevice.get(deviceName), devAddr);
    }

    public void close() {
        wireDevice.deregisterForPortReadyCallback();
        downQueue.close();
        upQueue.close();
        wireDevice.close();
    }

    //------------------------------------------------- Public Methods -------------------------
    public void beginWrite(int regNumber) {
        downStreamCache[CACHE_MODE] = WRITE_MODE;
        downStreamCache[DEV_ADDR] = wireDevAddress;
        downStreamCache[REG_NUMBER] = (byte) regNumber;
        downstreamNextLocation = DATA_OFFSET;
    }

    public void write(int value) {
        if (downstreamNextLocation >= LAST_INDEX) return;    // Max write size has been reached
        downStreamCache[downstreamNextLocation++] = (byte) value;
    }

    public void write(int regNumber, int value) {
        beginWrite(regNumber);
        write(value);
        endWrite();
    }

    public void writeHL(int regNumber, int value) {
        beginWrite(regNumber);
        write((byte) (value >> 8));
        write((byte) (value));
        endWrite();
    }

    public void writeLH(int regNumber, int value) {
        beginWrite(regNumber);
        write((byte) (value));
        write((byte) (value >> 8));
        endWrite();
    }

    public void endWrite() {
        downStreamCache[REG_COUNT] = (byte) (downstreamNextLocation - DATA_OFFSET);
        addRequest();
    }

    public void requestFrom(int regNumber, int regCount) {
        downStreamCache[CACHE_MODE] = READ_MODE;
        downStreamCache[DEV_ADDR] = wireDevAddress;
        downStreamCache[REG_NUMBER] = (byte) regNumber;
        downStreamCache[REG_COUNT] = (byte) regCount;
        addRequest();
    }

    public int responseCount() {
        int count = 0;
        try {
            readLock.lock();
            count = upQueue.length();
        } finally {
            readLock.unlock();
        }
        return count;
    }

    public boolean getResponse() {
        boolean responseReceived = false;
        upstreamNextCache = DATA_OFFSET;
        uLimit = upstreamNextCache;
        try {
            readLock.lock();                   // Explicit protection with readLock
            if (!upQueue.isEmpty()) {
                responseReceived = true;
                uMicros = getFromQueue(upstreamCache, upQueue);
                uLimit = upstreamNextCache + upstreamCache[REG_COUNT];
            }
        } finally {
            readLock.unlock();
        }
        return responseReceived;
    }

    public boolean isRead() {
        return (upstreamCache[CACHE_MODE] == READ_MODE);
    }

    public boolean isWrite() {
        return (upstreamCache[CACHE_MODE] == WRITE_MODE);
    }

    public int registerNumber() {
        return upstreamCache[REG_NUMBER] & 0xff;
    }

    public int deviceAddress() {
        return upstreamCache[DEV_ADDR] & 0xff;
    }

    public long micros() {
        return uMicros;
    }

    public int available() {
        return uLimit - upstreamNextCache;
    }

    public int read() {
        if (upstreamNextCache >= uLimit) return 0;
        return upstreamCache[upstreamNextCache++] & 0xff;
    }

    public int readHL() {
        int high = read();
        int low = read();
        return 256 * high + low;
    }

    public int readLH() {
        int low = read();
        int high = read();
        return 256 * high + low;
    }

//------------------------------------------------- Main routine: Device CallBack -------------

    public void portIsReady(int port) {
        if (isIdle) return;
        boolean isValidReply = false;
        try {
            readLock.lock();
            if (
                    readCache[CACHE_MODE] == writeCache[CACHE_MODE] &&
                            readCache[DEV_ADDR] == writeCache[DEV_ADDR] &&
                            readCache[REG_NUMBER] == writeCache[REG_NUMBER] &&
                            readCache[REG_COUNT] == writeCache[REG_COUNT]) {
                storeReceivedData();                        // Store read/write data
                isValidReply = true;
            }
        } finally {
            readLock.unlock();
        }
        if (isValidReply) {
            executeCommands();                              // Start next transmission
        } else {
            boolean isPollingRequired = false;
            try {
                writeLock.lock();                               // Protect the testing
                isPollingRequired = (writeCache[DEV_ADDR] == wireDevAddress);
            } finally {
                writeLock.unlock();
            }
            if (isPollingRequired) {
                wireDevice.readI2cCacheFromController();       // Keep polling active
            }
        }
    }

// --------------------------------- Commands to DIM -------------------------------------------

    private void executeCommands() {
        try {
            writeLock.lock();
            if (downQueue.isEmpty()) {
                isIdle = true;
            } else {
                getFromQueue(writeCache, downQueue); // Ignore timestamp
                writeCache[ACTION_FLAG] = -1;
            }
        } finally {
            writeLock.unlock();
        }
        if (!isIdle) {
            wireDevice.writeI2cCacheToController();
        }
    }

    private void addRequest() {
        boolean isStarting = false;
        try {
            writeLock.lock();
            if (isIdle) {
                int length = DATA_OFFSET + downStreamCache[REG_COUNT];
                System.arraycopy(downStreamCache, 0, writeCache, 0, length);
                writeCache[ACTION_FLAG] = -1;
                isIdle = false;
                isStarting = true;
            } else {
                addToQueue(0L, downStreamCache, downQueue);
            }
        } finally {
            writeLock.unlock();
        }
        if (isStarting) {
            wireDevice.writeI2cCacheToController();
        }
    }

// --------------------------------- PROCESSING OF RECEIVED DATA -------------------------------

    private void storeReceivedData() {
        // readCache has been locked
        long uMicros = (System.nanoTime() - startTime) / 1000L;
        addToQueue(uMicros, readCache, upQueue);
    }

//------------------------------------------------- Add and Remove from Queue ------------------

    private void addToQueue(long timeStamp, byte[] cache, ArrayQueue<Element> queue) {
        int length = DATA_OFFSET + cache[REG_COUNT];
        Element element = new Element();
        element.timeStamp = timeStamp;
        element.cache = new byte[length];
        System.arraycopy(cache, 0, element.cache, 0, length);
        queue.add(element);
    }

    private long getFromQueue(byte[] cache, ArrayQueue<Element> queue) {
        Element element = queue.remove();
        if (element == null) return 0;
        int length = element.cache.length;
        long timeStamp = element.timeStamp;
        System.arraycopy(element.cache, 0, cache, 0, length);
        return timeStamp;
    }

    private class Element {
        public long timeStamp;
        public byte[] cache;
    }
}
