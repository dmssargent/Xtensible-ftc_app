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

package org.ftccommunity.ftcxtensible.math;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class SamplingExecutor<T> {
    final T master;
    private final SamplingExecutor<SamplingExecutor<T>> upper;
    private final Numericalize<T> numericalize;
    private final LoadingCache<Integer, Double> cache;
    private final long timePerCycle;
    private final SecureRandom RNG = new SecureRandom();
    private int index = 0;
    private boolean stopped;
    private Thread executorThread;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            executorThread = Thread.currentThread();
            final long startTime = System.nanoTime();
            while (startTime + timePerCycle >= System.nanoTime()) {
                try {
                    cache.get(++index);
                } catch (ExecutionException e) {
                    Log.w("SAMPLER", e.getMessage());
                }
            }

            while (!Thread.currentThread().isInterrupted() || stopped) {
                cache.cleanUp();
                Thread.yield();
            }
        }
    };

    private SamplingExecutor(final T core, final Numericalize<T> numericalize, long howMuchDelayPerCycle, TimeUnit timeUnit, boolean upper) {
        this.upper = null;
        master = checkNotNull(core);
        this.numericalize = checkNotNull(numericalize);
        timePerCycle = TimeUnit.NANOSECONDS.convert(howMuchDelayPerCycle, timeUnit);
        cache = CacheBuilder.newBuilder().expireAfterWrite(howMuchDelayPerCycle, timeUnit).recordStats().softValues().removalListener(
                new RemovalListener<Integer, Double>() {
                    /**
                     * Notifies the listener that a removal occurred at some point in the past.
                     * <p/>
                     * <p>This does not always signify that the key is now absent from the cache, as it may have
                     * already been re-added.
                     */
                    @Override
                    public void onRemoval(RemovalNotification<Integer, Double> notification) {
                        if (notification.getKey() != null && cache.getIfPresent(notification.getKey()) == null) {
                            cache.put(notification.getKey(), SamplingExecutor.this.numericalize.toNumber(core));
                        } else if (notification.getKey() == null) {
                            cache.put(++index, numericalize.toNumber(core));
                        }
                    }
                }
        ).build(new CacheLoader<Integer, Double>() {
            @Override
            public Double load(Integer key) throws Exception {
                return numericalize.toNumber(core);
            }
        });
    }

    public SamplingExecutor(final T core, final Numericalize<T> numericalize, long howMuchDelayPerCycle, TimeUnit timeUnit) {
        master = checkNotNull(core);
        this.numericalize = checkNotNull(numericalize);
        timePerCycle = TimeUnit.NANOSECONDS.convert(howMuchDelayPerCycle, timeUnit);
        cache = CacheBuilder.newBuilder().expireAfterWrite(howMuchDelayPerCycle, timeUnit).recordStats().softValues().removalListener(
                new RemovalListener<Integer, Double>() {
                    /**
                     * Notifies the listener that a removal occurred at some point in the past.
                     * <p/>
                     * <p>This does not always signify that the key is now absent from the cache, as it may have
                     * already been re-added.
                     */
                    @Override
                    public void onRemoval(RemovalNotification<Integer, Double> notification) {
                        if (notification.getKey() != null && cache.getIfPresent(notification.getKey()) == null) {
                            cache.put(notification.getKey(), SamplingExecutor.this.numericalize.toNumber(core));
                        } else if (notification.getKey() == null) {
                            cache.put(++index, numericalize.toNumber(core));
                        }
                    }
                }
        ).build(new CacheLoader<Integer, Double>() {
            @Override
            public Double load(Integer key) throws Exception {
                return numericalize.toNumber(core);
            }
        });

        upper = new SamplingExecutor<>(this, new Numericalize<SamplingExecutor<T>>() {
            @Override
            public double toNumber(SamplingExecutor<T> object) {
                return object.sampleMean(index);
            }
        }, howMuchDelayPerCycle, timeUnit, true);
    }

    public List<Double> sampleOf(int n) {
        List<Double> sample = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int key = RNG.nextInt(index + 1);
            try {
                sample.add(cache.get(key));
                cache.invalidate(key);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return sample;
    }

    public double sampleMean(int n) {
        List<List<Double>> samples = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            samples.add(sampleOf(sampleSizeForMean()));
        }

        return Stat.mean(samples, new Numericalize<List<Double>>() {
            @Override
            public double toNumber(List<Double> object) {
                return Stat.mean(object, new Numericalize<Double>() {
                    @Override
                    public double toNumber(Double object) {
                        return object;
                    }
                });
            }
        });
    }

    public int sampleSizeForMean() {
        if (index > 30) {
            return 30;
        } else {
            return index;
        }
    }

    public void start() {
        if (executorThread == null) {
            Thread newThread = new Thread(runnable);
            newThread.setPriority(2);
            newThread.start();
            if (upper != null) {
                upper.start();
            }
        }
    }

    public Runnable runnable() {
        return runnable;
    }

    public void stop() {
        stopped = true;
    }

    public double predictedMean() {
        if (upper == null) {
            final int index = this.index;
            List<Double> avgList = new ArrayList<>(index);
            for (int i = 0; i < index; i++) {
                avgList.add(sampleMean(index));
            }

            return Stat.mean(avgList, new Numericalize<Double>() {
                @Override
                public double toNumber(Double object) {
                    return object;
                }
            });
        } else {
            return upper.predictedMean();
        }
    }

    public Runnable upperRunnable() {
        return upper.runnable;
    }
}
