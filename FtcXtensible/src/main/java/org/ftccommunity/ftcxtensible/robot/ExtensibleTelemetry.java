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

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multiset;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.collections.SynchronousArrayQueue;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Alpha
@NotDocumentedWell
public class ExtensibleTelemetry {
    public static final int DEFAULT_DATA_MAX = 192;
    public static final int MAX_DATA_MAX = 255;
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String TAG = ExtensibleTelemetry.class.getName();
    private final Telemetry parent;
    private final int dataPointsToSend;

    private final SynchronousArrayQueue<TelemetryItem> dataCache;
    private final LinkedHashMultimap<String, String> data;
    private final Cache<String, String> cache;
    private final Queue<String> log;

    // private Process logcat;
    // private BufferedReader reader;

    private long lastModificationTime;

    // private ScheduledExecutorService executorService;

    public ExtensibleTelemetry(@NotNull Telemetry telemetry) {
        this(DEFAULT_DATA_MAX, telemetry);
    }

    public ExtensibleTelemetry(int dataPointsToSend, @NotNull Telemetry telemetry) {
        checkArgument(dataPointsToSend < MAX_DATA_MAX);

        this.parent = telemetry;

        this.dataPointsToSend = dataPointsToSend;
        cache = CacheBuilder.newBuilder().
                concurrencyLevel(4).
                expireAfterAccess(250, TimeUnit.MILLISECONDS).
                maximumSize(dataPointsToSend).build();

        dataCache = new SynchronousArrayQueue<>(50);
        data = LinkedHashMultimap.create();
        log = new LinkedList<>();
    }

    public void data(String tag, String message) {
        checkArgument(!Strings.isNullOrEmpty(message), "Your message shouldn't be empty.");
        tag = Strings.nullToEmpty(tag);

        lastModificationTime = System.nanoTime();
        //parent.addData(tag, message);
        dataCache.add(new TelemetryItem(tag.equals(EMPTY) ? EMPTY : tag.toUpperCase(Locale.US) + SPACE, message));
    }

    public void addPersistentData(String tag, String mess) {
        synchronized (data) {
            lastModificationTime = System.nanoTime();
            data.put(tag, mess);
        }
    }

    public void data(String tag, double message) {
        data(tag, Double.toString(message));
    }

    public void data(String tag, Object object) {
        data(tag, object.toString());
    }

    synchronized void close() throws IOException {
//        executorService.shutdown();
//        reader.close();
//        logcat.destroy();
        synchronized (parent) {
            parent.clearAll();
        }
        synchronized (log) {
            log.clear();
        }

        //synchronized (dataCache) {
        //dataCache.clear();
        //}

        synchronized (data) {
            data.clear();
        }
        synchronized (cache) {
            cache.invalidateAll();
        }
    }

    void updateCache() {
        int cacheSize = (int) cache.size();
        if (lastModificationTime > 0) {
            forceUpdateCache();
        } else {
            cache.cleanUp();
            if (cacheSize > cache.size()) {
                forceUpdateCache();
            }
        }
    }

    synchronized void forceUpdateCache() {
        int numberOfElements;
        synchronized (cache) {
            cache.invalidateAll();

            //synchronized (dataCache) {
                int numberOfElementsAdded = 0;
            int min = Math.min(dataCache.length(), (int) (dataPointsToSend * .75));
                int stringLength = String.valueOf(min).length();
                for (; numberOfElementsAdded < min; numberOfElementsAdded++) {
                    final TelemetryItem remove = dataCache.remove();
                    cache.put(cancelOut(stringLength, String.valueOf(numberOfElementsAdded)), remove.getTag() + remove.getMessage());
                }

                numberOfElements = numberOfElementsAdded;
            //}

            synchronized (data) {
                numberOfElementsAdded = 0;
                HashMap<String, String> entries = new HashMap<>();

                LinkedList<Multiset.Entry<String>> keys = new LinkedList<>(data.keys().entrySet());
                for (Multiset.Entry<String> key : keys) {
                    LinkedList<String> dataElements = new LinkedList<>(data.get(key.getElement()));

                    int size = dataElements.size();

                    for (int index = 0; numberOfElementsAdded < size; numberOfElementsAdded++) {
                        entries.put(cancelOut(1, key.getElement() + Integer.toString(index)), dataElements.get(index));
                    }
                }

                try {
                    LinkedList<Map.Entry<String, String>> entriesToSend = new LinkedList<>(entries.entrySet());
                    for (; numberOfElementsAdded < Math.min(entriesToSend.size(), dataPointsToSend - numberOfElements);
                         numberOfElementsAdded++) {
                        Map.Entry<String, String> entry = entriesToSend.get(numberOfElementsAdded - 1);
                        cache.put(entry.getKey(), entry.getValue());
                    }
                } catch (IndexOutOfBoundsException ex) {
                    Log.d(TAG, "An index is out of bounds.", ex);
                }
            }
        }
    }

    synchronized void sendData() {
        updateCache();

        LinkedList<Map.Entry<String, String>> data;
        synchronized (cache) {
            data = new LinkedList<>(cache.asMap().entrySet());
        }
        for (Map.Entry<String, String> entry : data) {
            parent.addData(entry.getKey(), entry.getValue());
        }

        synchronized (log) {
            if (log.size() < dataPointsToSend) {
                int numberOfElementsAdded = 0;
                int min = Math.min(dataPointsToSend - log.size(), log.size());
                for (; numberOfElementsAdded < min; numberOfElementsAdded++) {
                    parent.addData("xLog" + String.valueOf(numberOfElementsAdded), log.poll());
                }
            }
        }
    }

    /**
     * Pads the end of a string with enough "\b" characters to cancel out the original string, if
     * it is every printed
     * @param string the string to cancel out
     * @return the string padded with {@code '\b'} characters
     */
    @NotNull
    private String cancelOut(int length, @NotNull String string) {
        return Strings.padStart(checkNotNull(string), length, '\b');
    }

    private class TelemetryItem {
        private String tag;
        private String message;

        private TelemetryItem(String tag, String message) {
            this.tag = tag;
            this.message = message;
        }

        public String getTag() {
            return tag;
        }

        public String getMessage() {
            return message;
        }
    }

//    private class SendDataRunnable implements Runnable {
//        /**
//         * Starts executing the active part of the class' code. This method is called when a thread is
//         * started that has been created with a class which implements {@code Runnable}.
//         */
//        @Override
//        public void run() {
//            try {
//                sendData();
//            } catch (Exception ex) {
//                Log.w(TAG, "Telemetry Sender threw an exception while executing.", ex);
//            }
//        }
//    }
}
