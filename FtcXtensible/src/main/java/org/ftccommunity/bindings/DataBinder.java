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
package org.ftccommunity.bindings;

import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Stack;

/**
 * A static class used to manage inter-library communication to avoid cyclic dependencies. This may
 * be a bit smelly, please submit a better idea if you have one
 *
 * @author David Sargent
 * @since 0.2.2
 */
public class DataBinder {
    /**
     * The view identity for the FTC SDK, you can access this value by
     * <code>DataBinder.getInstance().integers(DataBinder.RC_VIEW)</code>
     *
     * @see #integers()
     */
    public static final String RC_VIEW = "ftcview";

    public static final String RC_MANAGER = "ftcopmanager";
    public static final String CAMERA_VIEW = "ftccamera";

    private static DataBinder instance;
    private static HashMap<String, Integer> integerBindings;
    private static HashMap<String, View> viewBindings;
    private static HashMap<String, String> stringBindings;
    private static HashMap<String, Object> objectBindings;
    private static HashMap<String, Object> RC;

    /**
     * Creates new data binder instance, {@see #getInstance} if need to use this object for
     * management
     */
    private DataBinder() {
        integerBindings = new HashMap<>();
        viewBindings = new HashMap<>();
        stringBindings = new HashMap<>();
        objectBindings = new HashMap<>();

        instance = this;
    }

    /**
     * Creates a new {@code DataBinder} if there is not current instance, or returns an existing
     * instance for use
     *
     * @return the current DataBinder instance
     */
    public static DataBinder getInstance() {
        if (instance == null) {
            instance = new DataBinder();
        }

        return instance;
    }

    /**
     * Returns a Map dedicated to storing Integers
     *
     * @return integer map
     */
    public HashMap<String, Integer> integers() {
        return integerBindings;
    }

    /**
     * Returns a Map dedicated to storing various {@code View}
     *
     * @return view map
     * @see View
     */
    public HashMap<String, View> views() {
        return viewBindings;
    }

    /**
     * Returns a Map dedicated to storing {@code String}
     *
     * @return {@code String} map
     */
    public HashMap<String, String> strings() {
        return stringBindings;
    }

    /**
     * Retuns a Map dedicated to storing generic Objects that do not fit other categories well
     *
     * @return a generic {@code Object} map
     */
    public HashMap<String, Object> objects() {
        return objectBindings;
    }

    public int id(String name) {
        String key = "R.id." + name;
        if (RC.containsKey(key)) {
            return (int) RC.get(key);
        } else {
            throw new IllegalArgumentException("ID Not Found");
        }
    }

    public void bindTo(Class<?> R) {
        Stack<String> currentStack = new Stack<>();
        currentStack.add(R.getSimpleName());
        RC = new HashMap<>();
        addInners(currentStack, RC, R);
    }

    private void addInners(Stack<String> stack, HashMap<String, Object> map, Class<?> klazz) {
        for (Class<?> innerClass : klazz.getClasses()) {
            stack.add("." + innerClass.getSimpleName().replace('$', '\0'));
            String currentLocation = "";
            for (String element : stack) {
                currentLocation += element;
            }
            for (Field o : innerClass.getFields()) {
                boolean wasAccessible = o.isAccessible();
                if (Modifier.isStatic(o.getModifiers())) {
                    try {
                        map.put(currentLocation + "." + o.getName(), o.get(null));
                    } catch (IllegalAccessException | NullPointerException e) {
                        Log.w("BINDINGS", "The field \"" + o.getName() + "\" failed to be accessed", e);
                    }
                }
                o.setAccessible(wasAccessible);
            }

            addInners(stack, map, innerClass);
            stack.pop();
        }
    }
}
