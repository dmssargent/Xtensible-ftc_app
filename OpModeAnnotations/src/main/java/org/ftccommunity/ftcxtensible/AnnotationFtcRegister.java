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

package org.ftccommunity.ftcxtensible;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.util.Util;

import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import dalvik.system.DexFile;

/**
 * An manager that reads in the OpMode classes
 *
 * @author David Sargent
 * @version 0.2.1
 */
@NotDocumentedWell
@Alpha
public class AnnotationFtcRegister {
    private static final String TAG = "FTC_OP_MODE_REGISTER::";

    private final LinkedList<String> noCheckList;

    private AnnotationFtcRegister() {
        noCheckList = new LinkedList<>();
    }

    public static void loadOpModes(OpModeManager register) {
        AnnotationFtcRegister me = new AnnotationFtcRegister();
        me.buildNoCheckList();

        me.buildClassList();
        HashMap<String, LinkedList<Class<OpMode>>> opModes = me.findOpModes(me.buildClassList());
        me.sortOpModeMap(opModes);

        LinkedList<Class<OpMode>> opModesToRegister = new LinkedList<>();
        for (LinkedList<Class<OpMode>> opModeList : me.treeMapify(opModes).values()) {
            for (Class<OpMode> opMode : opModeList) {
                // todo: move this to the appropriate section
                if (opMode.isAnnotationPresent(RobotSdkVersion.class)) {
                    RobotSdkVersion annotation = opMode.getAnnotation(RobotSdkVersion.class);
                    if (annotation.value() != RobotSdkApiLevel.currentVerison()) {
                        if (annotation.strict() ||
                                annotation.compatibleUpTo()
                                        .compareTo(RobotSdkApiLevel.currentVerison()) < 0) {
                            Log.w(TAG, "Skipping OpMode " + getOpModeName(opMode) +
                                    " because of incompatibility");
                            continue;
                        }
                    }
                }
                // register.register(getOpModeName(opMode), opMode);
                opModesToRegister.add(opMode);
            }
        }

        // check OpModes name length
        StringBuilder nameBuilder = new StringBuilder();
        for (Class<OpMode> opMode : opModesToRegister) {
            nameBuilder.append(getOpModeName(opMode)).append(Util.ASCII_RECORD_SEPARATOR);
        }

        int length = nameBuilder.toString().getBytes(Charset.defaultCharset()).length;
        if (length > 255) {
            Log.e(TAG, "OpMode names are " + (length - 255) +
                    " too long, please rename them or shorten them");
            register.register("Too Many OpMode Names", TooManyOpModes.class);
        } else {
            for (Class<OpMode> opMode :
                    opModesToRegister) {
                register.register(getOpModeName(opMode), opMode);
            }
        }
    }

    private void buildNoCheckList() {
        noCheckList.add("com.google");
        noCheckList.add("io.netty");
    }

    private Context getApplicationContext() {
        final Class<?> activityThreadClass;
        Context ctx = null;

        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");

            final Method method = activityThreadClass.getMethod("currentApplication");
            ctx = (Application) method.invoke(null, (Object[]) null);
        } catch (ClassNotFoundException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            Log.wtf(TAG, e);
        }

        return ctx;
    }

    private LinkedList<Class> buildClassList() {
        DexFile df;
        Context applicationContext = getApplicationContext();

        // Try to load the Dex-File
        try {
            df = new DexFile(applicationContext.getPackageCodePath());
        } catch (IOException e) {
            Log.wtf(TAG, e);
            throw new NullPointerException();
        }


        // Migrate the enum to OpMode LinkedList
        LinkedList<String> classes = new LinkedList<>(Collections.list(df.entries()));

        // Create the container for the objects to add
        LinkedList<Class> classesToProcess = new LinkedList<>();
        for (String klazz : classes) {
            if (shouldAdd(klazz)) {
                try {
                    classesToProcess.add(Class.forName(klazz, false, applicationContext.getClassLoader()));
                } catch (ClassNotFoundException|NoClassDefFoundError e) {
                    Log.e(TAG, "Cannot add class to process list: " + klazz + "Reason: " + e.toString());

                    // Add code to prevent loading the next class
                    if (klazz.contains("$")) {
                        klazz = klazz.substring(0, klazz.indexOf("$") - 1);
                    }
                    noCheckList.add(klazz);
                }
            }
        }

        return classesToProcess;
    }

    private void sortOpModeMap(HashMap<String, LinkedList<Class<OpMode>>> opModes) {
        Comparator<Class> firstComparator =
                new OpModeComparator();

        for (String key : opModes.keySet()) {
            Collections.sort(opModes.get(key), firstComparator);
        }
    }


    private HashMap<String, LinkedList<Class<OpMode>>> findOpModes(LinkedList<Class> klazzes) {
        Class<TeleOp> teleOpClass = TeleOp.class;
        Class<Autonomous> autoClass = Autonomous.class;
        Class<Disabled> disabledClass = Disabled.class;

        int nextAvailableIndex = 0;
        HashMap<String, LinkedList<Class<OpMode>>> opModes = new HashMap<>();
        for (Class<OpMode> currentClass : klazzes) {
            if (!currentClass.isAnnotationPresent(disabledClass)) {
                if (currentClass.isAnnotationPresent(teleOpClass)) {
                    Annotation annotation = currentClass.getAnnotation(teleOpClass);
                    String name = ((TeleOp) annotation).pairWithAuto();
                    if (name.equals("")) {
                        int i = ++nextAvailableIndex;
                        LinkedList<Class<OpMode>> temp = new LinkedList<>();
                        temp.add(currentClass);
                        opModes.put(Integer.toString(i), temp);
                    } else {
                        if (opModes.containsKey(name)) {
                            opModes.get(name).add(currentClass);
                        } else {
                            LinkedList<Class<OpMode>> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(name, temp);
                        }
                    }
                }
                if (currentClass.isAnnotationPresent(autoClass)) {
                    Annotation annotation = currentClass.getAnnotation(autoClass);
                    String name = ((Autonomous) annotation).pairWithTeleOp();
                    if (name.equals("")) {
                        int i = ++nextAvailableIndex;
                        LinkedList<Class<OpMode>> temp = new LinkedList<>();
                        temp.add(currentClass);
                        opModes.put(Integer.toString(i), temp);
                    } else {
                        if (opModes.containsKey(name)) {
                            opModes.get(name).add(currentClass);
                        } else {
                            LinkedList<Class<OpMode>> temp = new LinkedList<>();
                            temp.add(currentClass);
                            opModes.put(name, temp);
                        }
                    }
                }
            }
        }
        return opModes;
    }

    private boolean shouldAdd(String klazz) {
        // Check against every class name in the no-add list
        for (String noContinue : noCheckList) {
            if (klazz.contains(noContinue)) {
                return false;
            }
        }
        return true;
    }

    private TreeMap<String, LinkedList<Class<OpMode>>> treeMapify(
            HashMap<String, LinkedList<Class<OpMode>>> opModes) {

        TreeMap<String, LinkedList<Class<OpMode>>> sortedOpModes = new TreeMap<>();
        for (String key : opModes.keySet()) {
            Class<OpMode> opMode = opModes.get(key).getFirst();
            String name = getOpModeName(opMode);

            sortedOpModes.put(name, opModes.get(key));
        }

        return sortedOpModes;
    }

    private static String getOpModeName(Class<OpMode> opMode) {
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

    private static class OpModeComparator implements Comparator<Class> {
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
    }

    private class TooManyOpModes extends OpMode {
        @Override
        public void init() {
            telemetry.addData("", "Too Many OpModes have been found, taking up too many" +
                    "bytes");
        }

        @Override
        public void loop() {
            telemetry.addData("", "Too Many OpModes have been found, taking up too many" +
                    "bytes");
        }
    }
}
