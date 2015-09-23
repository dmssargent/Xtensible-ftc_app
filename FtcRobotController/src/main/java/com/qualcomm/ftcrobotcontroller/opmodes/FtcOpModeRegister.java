/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import org.ftc.opmodes.FallbackOpModeRegister;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import dalvik.system.DexFile;

/**
 *
 * @author David Sargent - FTC5395, maths222, Qualcomm
 * @since 1.0
 */
public class FtcOpModeRegister implements OpModeRegister {
    private static String TAG = "FTC_OP_MODE_REGISTER::";

    /**
     * The Op Mode Manager will call this method when it wants stopMode list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     * @param mgr op mode manager
     */
    public void register(final OpModeManager mgr) {
        // manager.register(NetworkOpMode.class.getSimpleName(), new NetworkOpMode());
        try {
            FallbackOpModeRegister userFallback = new FallbackOpModeRegister();
            userFallback.register(mgr);

            final LinkedList<String> noCheckList = new LinkedList<>();
            noCheckList.add("com.google");
            noCheckList.add("io.netty");

            // Get the possible classes
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            Context context = (Application) method.invoke(null, (Object[]) null);
            DexFile df = new DexFile(context.getPackageCodePath());

            // Put the correct classes in stopMode HashMap out of the possible classes
            HashMap<String, LinkedList<Class>> opModes = new HashMap<>();

            // Migrate the enum to stopMode LinkedList
            LinkedList<String> classes = new LinkedList<>(Collections.list(df.entries()));
            for (String klazz : classes) {
                boolean shouldBreak = false;
                for (String noContinue : noCheckList) {
                    if (klazz.contains(noContinue)) {
                        shouldBreak = true;
                        break;
                    }
                }
                if (shouldBreak) {
                    continue;
                }

                Class teleOpClass = TeleOp.class;
                Class autoClass = Autonomous.class;
                Class disabledClass = Disabled.class;

                Class currentClass;
                try {
                    currentClass = Class.forName(klazz, false, context.getClassLoader());
                } catch (NoClassDefFoundError ex) {
                    Log.w(TAG, klazz + " " + ex.toString(), ex);
                    if (klazz.contains("$")) {
                        klazz = klazz.substring(0, klazz.indexOf("$") - 1);
                    }
                    noCheckList.add(klazz);
                    break;
                } catch (ClassNotFoundException ex) {
                    Log.w(TAG, klazz + " " + ex.toString(), ex);
                    if (klazz.contains("$")) {
                        klazz = klazz.substring(0, klazz.indexOf("$") - 1);
                    }
                    noCheckList.add(klazz);
                    break;
                }
                if (!currentClass.isAnnotationPresent(disabledClass)) {
                    if (currentClass.isAnnotationPresent(teleOpClass)) {
                        Annotation annotation = currentClass.getAnnotation(teleOpClass);
                        String name = ((TeleOp) annotation).pairWithAuto();
                        if (name.equals("")) {
                            int i = 0;
                            while (opModes.containsKey(Integer.toString(i))) {
                                i++;
                            }
                            LinkedList<Class> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(Integer.toString(i), temp);
                        } else {
                            if (opModes.containsKey(name)) {
                                opModes.get(name).add(currentClass);
                            } else {
                                LinkedList<Class> temp = new LinkedList<>();
                                temp.add(currentClass);
                                opModes.put(name, temp);
                            }
                        }
                    }
                    if (currentClass.isAnnotationPresent(autoClass)) {
                        Annotation annotation = currentClass.getAnnotation(autoClass);
                        String name = ((Autonomous) annotation).pairWithTeleOp();
                        if (name.equals("")) {
                            int i = 0;
                            while (opModes.containsKey(Integer.toString(i))) {
                                i++;
                            }
                            LinkedList<Class> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(Integer.toString(i), temp);
                        } else {
                            if (opModes.containsKey(name)) {
                                opModes.get(name).add(currentClass);
                            } else {
                                LinkedList<Class> temp = new LinkedList<>();
                                temp.add(currentClass);
                                opModes.put(name, temp);
                            }
                        }
                    }
                }
            }

            // Sort the linked lists within the Hash Map
            Comparator<Class> firstComparator =
                    new Comparator<Class>() {
                        @Override
                        public int compare(Class lhs, Class rhs) {
                            if (lhs.isAnnotationPresent(TeleOp.class) &&
                                    rhs.isAnnotationPresent(TeleOp.class)) {
                                String lName = ((TeleOp) lhs.getAnnotation(TeleOp.class)).name();
                                String rName = ((TeleOp) lhs.getAnnotation(TeleOp.class)).name();

                                if (lName.equals("")) {
                                    lName = lhs.getSimpleName();
                                }

                                if (rName.equals("")) {
                                    rName = rhs.getSimpleName();
                                }
                                return lName.compareTo(rName);
                            }

                            if (lhs.isAnnotationPresent(Autonomous.class) &&
                                    rhs.isAnnotationPresent(TeleOp.class)) {
                                return 1;
                            } else if (rhs.isAnnotationPresent(Autonomous.class) &&
                                    lhs.isAnnotationPresent(TeleOp.class)) {
                                return -1;
                            }

                            if (lhs.isAnnotationPresent(Autonomous.class) &&
                                    rhs.isAnnotationPresent(Autonomous.class)) {
                                String lName = ((Autonomous) lhs.getAnnotation(Autonomous.class)).name();
                                String rName = ((Autonomous) rhs.getAnnotation(Autonomous.class)).name();

                                if (lName.equals("")) {
                                    lName = lhs.getSimpleName();
                                }

                                if (rName.equals("")) {
                                    rName = rhs.getSimpleName();
                                }
                                return lName.compareTo(rName);
                            }

                            return -1;
                        }
                    };
            for (String key : opModes.keySet()) {
                Collections.sort(opModes.get(key), firstComparator);
            }

            // Sort the map by keys, after discarding the old keys, use the new key from
            // the first item in each LinkedList, and change from stopMode HashMap to stopMode TreeMap
            TreeMap<String, LinkedList<Class>> sortedOpModes = new TreeMap<>();
            for (String key : opModes.keySet()) {
                Class<? extends OpMode> opMode = opModes.get(key).getFirst();
                String name;
                if (opMode.isAnnotationPresent(TeleOp.class)) {
                    name = opMode.getAnnotation(TeleOp.class).name();
                } else if (opMode.isAnnotationPresent(Autonomous.class)) {
                    name = opMode.getAnnotation(Autonomous.class).name();
                } else {
                    name = opMode.getSimpleName();
                }

                if (name.equals("")) {
                    name = opMode.getSimpleName();
                }
                sortedOpModes.put(name, opModes.get(key));
            }

            for (LinkedList<Class> opModeList : sortedOpModes.values()) {
                for (Class opMode : opModeList) {
                    mgr.register(getOpModeName(opMode), opMode);
                }
            }

        } catch (final ClassNotFoundException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (final NoSuchMethodException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (final IllegalArgumentException e) {
            Log.e(TAG, e.toString(), e);
        } catch (final IllegalAccessException e) {
            Log.e(TAG, e.toString(), e);
        } catch (final InvocationTargetException e) {
            Log.wtf(TAG, e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }

        /*try {
            final LinkedList<String> noCheckList = new LinkedList<>();
            noCheckList.add("com.google");
            noCheckList.add("io.netty");
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            Context context = (Application) method.invoke(null, (Object[]) null);
            DexFile df = new DexFile(context.getPackageCodePath());
            Thread process = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Class<?> activityThreadClass;
                    try {
                        activityThreadClass = Class.forName("android.app.ActivityThread");

                        final Method method = activityThreadClass.getMethod("currentApplication");
                        Context context = (Application) method.invoke(null, (Object[]) null);
                        DexFile df = new DexFile(context.getPackageCodePath());

                        LinkedList<String> classes = new LinkedList<>(Collections.list(df.entries()));
                        for (String klazz : classes) {
                            boolean shouldBreak = false;
                            for (String noContinue : noCheckList) {
                                if (klazz.contains(noContinue)) {
                                    shouldBreak = true;
                                    break;
                                }
                            }
                            if (shouldBreak) {
                                continue;
                            }

                            Class activeClass = ActiveOpMode.class;
                            Class currentClass;
                            try {
                                currentClass = Class.forName(klazz, false, context.getClassLoader());
                            } catch (NoClassDefFoundError ex) {
                                Log.w(TAG, klazz + " " + ex.toString(), ex);
                                return;
                            } catch (ClassNotFoundException ex) {
                                Log.w(TAG, klazz + " " + ex.toString(), ex);
                                return;
                            }
                            Log.i("CLASSES", currentClass.toString());
                            if(currentClass.isAnnotationPresent(activeClass)) {
                                Annotation annotation = currentClass.getAnnotation(activeClass);
                                String name = ((ActiveOpMode) annotation).value();
                                if(name.length() == 0) {
                                    name = currentClass.getSimpleName();
                                }
                                manager.register(name, currentClass);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            process.start();
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final NoSuchMethodException e) {
            // handle exception
        } catch (final IllegalArgumentException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        } catch (final InvocationTargetException e) {
            // handle exception
        } catch (IOException e) {
            // handle exception
        }*/
    }


    public String getOpModeName(Class<OpMode> opMode) {
        String name;
        if (opMode.isAnnotationPresent(TeleOp.class)) {
            name = opMode.getAnnotation(TeleOp.class).name();
        } else if (opMode.isAnnotationPresent(Autonomous.class)) {
            name = opMode.getAnnotation(Autonomous.class).name();
        } else {
            name = opMode.getSimpleName();
        }

        if (name.equals("")) {
            name = opMode.getSimpleName();
        }
        return name;
    }
}
