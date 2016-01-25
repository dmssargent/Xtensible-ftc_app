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

package org.ftcommunity.ftcxtensible.core.internal;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Created by David on 1/24/2016.
 */
public final class HashUtil {
    int hash;

    public HashUtil(int seed) {
        hash = seed;
    }

    public static HashUtil create(int seed) {
        return new HashUtil(seed);
    }

    public static int addFieldToHash(int orig, @Nullable Object add) {
        if (add == null) {
            return addNullToHash(orig);
        } else {
            return 31 * orig + add.hashCode();
        }
    }

    public static int addFieldToHash(int orig, boolean add) {
        return 31 * orig + (add ? 1 : 0);
    }

    public static int addFieldToHash(int orig, int add) {
        return 31 * orig + add;
    }

    public static int addFieldToHash(int orig, long add) {
        return (int) (31 * orig + (add ^ (add >>> 32)));
    }

    public static int addFieldToHash(int orig, float add) {
        return 31 * orig + Float.floatToIntBits(add);
    }

    public static int addFieldToHash(int orig, double add) {
        return addFieldToHash(orig, Double.doubleToLongBits(add));
    }

    public static <T> int addFieldToHash(int orig, @Nullable T[] add) {
        if (add == null) {
            return addNullToHash(orig);
        } else {
            return 31 * orig + Arrays.deepHashCode(add);
        }
    }

    private static int addNullToHash(int orig) {
        return 31 * orig;
    }

    public HashUtil addFieldToHash(@Nullable Object add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public HashUtil addFieldToHash(boolean add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public HashUtil addFieldToHash(int add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public HashUtil addFieldToHash(long add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public HashUtil addFieldToHash(float add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public HashUtil addFieldToHash(double add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public <T> HashUtil addFieldToHash(@Nullable T[] add) {
        hash = addFieldToHash(hash, add);
        return this;
    }

    public int get() {
        return hash;
    }
}
