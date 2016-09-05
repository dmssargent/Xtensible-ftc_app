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

package org.ftccommunity.ftcxtensible.dagger;

import android.util.Log;

import org.jetbrains.annotations.Contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ReflectionUtilities {
    public static boolean isParent(Class<?> klazz, Class<?> testParent) {
        List<Class> candidates = new LinkedList<>();
        testParent = checkNotNull(testParent, "testParent is null");
        candidates.add(checkNotNull(klazz, "klazz is null").getSuperclass());
        candidates.addAll(Arrays.asList(klazz.getInterfaces()));

        if (candidates.size() == 1) {
            Class candidate = candidates.get(0);
            Log.d(ReflectionUtilities.class.getSimpleName(), "Comparing " + klazz.getSimpleName() + " with " + testParent.getSimpleName());
            if (candidate == null) {
                return false;
            } else if (testParent.equals(klazz)) {
                return true;
            } else if (klazz.equals(Object.class)) {
                return false;
            } else if (isParent(candidate, testParent)) {
                return true;
            }
        } else {
            if (!klazz.equals(Object.class) && candidates.contains(Object.class)) {
                candidates.remove(Object.class);
            }
            for (Class<?> candidate : candidates) {
                Log.d(ReflectionUtilities.class.getSimpleName(), "Comparing " + klazz.getSimpleName() + " with " + testParent.getSimpleName());
                if (candidate == null) {
                    continue;
                }
                if (candidate.equals(testParent)) {
                    return true;
                } else if (isParent(candidate, testParent)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasMethodAnnotationsOf(Class<? extends Annotation> annotation, Class<?> forThis) {
        for (Method method : forThis.getMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                return true;
            }
        }

        return false;
    }

    @Contract(value = "null -> true; !null -> false", pure = true)
    static <T> boolean isNull(T instance) {
        return instance == null;
    }
}
