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
import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Various internal utilities that assist us.
 */
public class Util {
    //----------------------------------------------------------------------------------------------
    // Hardware mappings
    //----------------------------------------------------------------------------------------------

    // The Field of the 'fieldDexIndex' field of class Field
    static Field fieldDexIndexField = getFieldDexIndexField();
    static Comparator<Field> fieldComparator = new Comparator<Field>()
            // A comparator that sorts fields according to their declaration order
    {
        @Override
        public int compare(Field a, Field b) {
            return getSortIndex(a) - getSortIndex(b);
        }
    };

    public static List<HardwareMap.DeviceMapping<?>> deviceMappings(HardwareMap map)
    // Returns all the device mappings within the map
    {
        List<HardwareMap.DeviceMapping<?>> result = new LinkedList<>();
        result.add(map.dcMotorController);
        result.add(map.servoController);
        result.add(map.legacyModule);
        result.add(map.deviceInterfaceModule);
        result.add(map.colorSensor);
        result.add(map.dcMotor);
        result.add(map.gyroSensor);
        result.add(map.servo);
        result.add(map.analogInput);
        result.add(map.digitalChannel);
        result.add(map.opticalDistanceSensor);
        result.add(map.touchSensor);
        result.add(map.pwmOutput);
        result.add(map.i2cDevice);
        result.add(map.analogOutput);
        result.add(map.led);
        result.add(map.accelerationSensor);
        result.add(map.compassSensor);
        result.add(map.irSeekerSensor);
        result.add(map.lightSensor);
        result.add(map.ultrasonicSensor);
        result.add(map.voltageSensor);
        result.add(map.touchSensorMultiplexer);
        return result;
    }

    public static <T> void remove(HardwareMap.DeviceMapping<T> from, IFuncArg<Boolean, T> predicate, IAction<T> action) {
        List<String> names = new LinkedList<>();
        for (Map.Entry<String, T> pair : from.entrySet()) {
            T t = pair.getValue();
            if (predicate == null || predicate.value(t)) {
                names.add(pair.getKey());
                if (action != null) action.doAction(t);
            }
        }
        for (String name : names) {
            removeName(from, name);
        }
    }

    public static <T> void removeName(HardwareMap.DeviceMapping<T> entrySet, String name) {
        Util.<Map>getPrivateObjectField(entrySet, 0).remove(name);
    }

    public static <T> boolean contains(HardwareMap.DeviceMapping<T> map, String name) {
        for (Map.Entry<String, T> pair : map.entrySet()) {
            if (pair.getKey().equals(name))
                return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // String
    //----------------------------------------------------------------------------------------------

    /**
     * Is 'prefix' an initial substring of 'target'?
     */
    static public boolean isPrefixOf(String prefix, String target) {
        if (prefix == null)
            return true;
        else if (target == null)
            return false;
        else {
            if (prefix.length() <= target.length()) {
                for (int ich = 0; ich < prefix.length(); ich++) {
                    if (prefix.charAt(ich) != target.charAt(ich))
                        return false;
                }
                return true;
            }
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Threads
    //----------------------------------------------------------------------------------------------

    public static void shutdownAndAwaitTermination(ExecutorService service) {
        service.shutdown();
        awaitTermination(service);
    }

    public static void shutdownNowAndAwaitTermination(ExecutorService service) {
        service.shutdownNow();
        awaitTermination(service);
    }

    public static void awaitTermination(ExecutorService service) {
        try {
            service.awaitTermination(30, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Util.handleCapturedInterrupt(e);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Miscellany
    //----------------------------------------------------------------------------------------------

    static public double milliseconds(ElapsedTime elapsed) {
        return elapsed.time() * 1000.0;
    }

    static public String getStackTrace(Exception e) {
        StringBuilder result = new StringBuilder();
        result.append(e.toString());
        for (StackTraceElement ele : e.getStackTrace()) {
            result.append("\n");
            result.append(ele.toString());
        }
        return result.toString();
    }

    public static byte[] concatenateByteArrays(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    //----------------------------------------------------------------------------------------------
    // Private method access
    //
    // Ugh. We wish we didn't have to do this. But the definitions of some classes we need
    // to override leave us no choice.
    //----------------------------------------------------------------------------------------------

    static List<Method> getDeclaredMethods(Class<?> clazz)
    // Guard against silly class loaders
    {
        Method[] methods;
        try {
            methods = clazz.getDeclaredMethods();
        } catch (Exception e) {
            methods = new Method[0];
        }
        List<Method> result = new LinkedList<>();
        result.addAll(Arrays.asList(methods));
        return result;
    }

    static public List<Method> getDeclaredMethodsIncludingSuper(Class<?> clazz) {
        if (clazz.getSuperclass() == null) {
            return getDeclaredMethods(clazz);
        } else {
            List<Method> result = getDeclaredMethodsIncludingSuper(clazz.getSuperclass());
            result.addAll(getDeclaredMethods(clazz));
            return result;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Private field access - utility
    //----------------------------------------------------------------------------------------------

    static Field getFieldDexIndexField()
    // Find the Field for the 'fieldDexIndex' field of class Field.
    {
        Class fieldClass = Field.class;
        Class fieldSuperClass = fieldClass.getSuperclass();

        List<Field> superFields = getLocalDeclaredNonStaticFields(fieldSuperClass, false);
        List<Field> fieldFields = getLocalDeclaredNonStaticFields(fieldClass, false);

        int iFieldTarget = 7;

        Field result = fieldFields.get(iFieldTarget - superFields.size());
        if (!result.isAccessible())
            result.setAccessible(true);

        return result;
    }

    static int getSortIndex(Field field)
    // Returns a sort key that will sort Fields in their declared order
    {
        try {
            return fieldDexIndexField.getInt(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static List<Field> getLocalDeclaredNonStaticFields(Class<?> clazz) {
        return getLocalDeclaredNonStaticFields(clazz, true);
    }

    static List<Field> getLocalDeclaredNonStaticFields(Class<?> clazz, boolean sort) {
        List<Field> result = new LinkedList<>();
        for (Field field : Arrays.asList(clazz.getDeclaredFields())) {
            final int requiredModifiers = 0;
            final int prohibitedModifiers = Modifier.STATIC;

            if ((0) == requiredModifiers
                    && (field.getModifiers() & prohibitedModifiers) == 0) {
                result.add(field);
            }
        }

        if (sort) Collections.sort(result, Util.fieldComparator);
        return result;
    }

    //----------------------------------------------------------------------------------------------
    // Private field Field access - local
    //----------------------------------------------------------------------------------------------

    static public List<Field> getDeclaredNonStaticFieldsIncludingSuper(Class<?> clazz, boolean sort) {
        if (clazz.getSuperclass() == null) {
            return getLocalDeclaredNonStaticFields(clazz, sort);
        } else {
            List<Field> result = getDeclaredNonStaticFieldsIncludingSuper(clazz.getSuperclass(), sort);
            result.addAll(getLocalDeclaredNonStaticFields(clazz, sort));
            return result;
        }
    }

    static public Field getAccessibleClassNonStaticFieldIncludingSuper(Object target, int iField) {
        Class<?> c = target.getClass();
        List<Field> fields = getDeclaredNonStaticFieldsIncludingSuper(c, true);
        Field field = fields.get(iField);

        if (!field.isAccessible())
            field.setAccessible(true);

        return field;
    }

    //----------------------------------------------------------------------------------------------
    // Private field Field access - super
    //----------------------------------------------------------------------------------------------

    static public Field getLocalAccessibleClassNonStaticField(Object target, int iField) {
        Class<?> c = target.getClass();
        List<Field> fields = getLocalDeclaredNonStaticFields(c, true);
        Field field = fields.get(iField);

        if (!field.isAccessible())
            field.setAccessible(true);

        return field;
    }

    static public int getPrivateIntField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        return getPrivateIntField(target, field);
    }

    static public int getPrivateIntField(Object target, Field field) {
        try {
            return field.getInt(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Private field access
    //----------------------------------------------------------------------------------------------

    static public long getPrivateLongField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getLong(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public short getPrivateShortField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getShort(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public double getPrivateDoubleField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getDouble(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public float getPrivateFloatField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getFloat(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public boolean getPrivateBooleanField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getBoolean(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public byte getPrivateByteField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            return field.getByte(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // @SuppressWarnings("unchecked")
    static public <T> T getPrivateObjectField(Object target, int iField) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        return Util.getPrivateObjectField(target, field);
    }

    static public <T> T getLocalPrivateObjectField(Object target, int iField) {
        Field field = getLocalAccessibleClassNonStaticField(target, iField);
        return Util.getPrivateObjectField(target, field);
    }

    static public <T> T getPrivateObjectField(Object target, Field field) {
        try {
            return (T) (field.get(target));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public <T> void setPrivateObjectField(Object target, int iField, T value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateLongField(Object target, int iField, long value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setLong(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateIntField(Object target, int iField, int value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setInt(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateByteField(Object target, int iField, byte value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setByte(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateBooleanField(Object target, int iField, boolean value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setBoolean(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateFloatField(Object target, int iField, float value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setFloat(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setPrivateDoubleField(Object target, int iField, double value) {
        Field field = getAccessibleClassNonStaticFieldIncludingSuper(target, iField);
        try {
            field.setDouble(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleCapturedInterrupt(InterruptedException e) {
        handleCapturedException(e);
    }

    public static void handleCapturedException(Exception e) {
        if (e instanceof InterruptedException) ;
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
    }

    //----------------------------------------------------------------------------------------------
    // Dealing with captured exceptions
    //
    // That InterruptedException is a non-runtime exception is, I believe, a bug: I could go
    // on at great length here about that, but for the moment will refrain and defer until another
    // time: the issue is a lengthy discussion.
    //
    // But we have the issue of what to do. That the fellow has captured the interrupt means that
    // he doesn't want an InterruptedException to propagate. Yet somehow we must in effect do so:
    // the thread needs to be torn down. Ergo, we seem to have no choice but to throw a runtime
    // version of the interrupt.
    //----------------------------------------------------------------------------------------------

    public interface IFuncArg<T, U> {
        T value(U u);
    }

    public interface IAction<T> {
        void doAction(T t);
    }
}
