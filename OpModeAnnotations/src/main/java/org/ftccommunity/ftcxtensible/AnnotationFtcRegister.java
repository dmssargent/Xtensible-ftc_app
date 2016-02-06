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

package org.ftccommunity.ftcxtensible;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Util;

import dalvik.system.DexFile;

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
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

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

    /**
     * This statically loads OpModes via reflection. This is the main method for this procedure
     *
     * @param register the Ftc OpMode Register
     */
    public static void loadOpModes(OpModeManager register) {
        AnnotationFtcRegister me = new AnnotationFtcRegister();
        me.buildNoCheckList();

        LinkedList<Class> classes = me.buildClassList();
        Class[] classArray = classes.toArray(new Class[classes.size()]);
        ServiceRegister.init(classArray);
        HashMap<String, LinkedList<Class<OpMode>>> opModes = me.findOpModes(classes);
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

                // todo workaround an the issue of reflectively loading an non-public class
                // fails silenty
                if (!Modifier.isPublic(opMode.getModifiers())) {
                    RobotLog.setGlobalErrorMsg(getOpModeName(opMode) + " is marked as an OpMode" +
                            ", however it is not public. Please add the keyword 'public' to it");
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
            RobotLog.setGlobalErrorMsg("OpMode names are " + (length - 255) +
                    " too long, please rename them or shorten them");
            register.register("Too Many OpMode Names", TooManyOpModes.class);
        } else {
            for (Class<OpMode> opMode :
                    opModesToRegister) {
                register.register(getOpModeName(opMode), opMode);
            }
        }
    }

    /**
     * This gets the correct name of the OpMode via reflection. This returns the value of the name
     * present in the Annation or Simple Name of the class.
     *
     * @param opMode the OpMode to check
     * @return the name to use of defined by the user
     */
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

    /**
     * Creates a list of packages not to check to improve errors and reduce logging
     */
    private void buildNoCheckList() {
        noCheckList.add("com.google");
        noCheckList.add("io.netty");
    }

    /**
     * This generates an Application Context to use for getting the class package loader.
     *
     * @return an new Application Context, this Context will throw a {@link ClassCastException} if
     * it is cast to an Activity
     */
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

    /**
     * Uses a context generated by {@link #getApplicationContext()} to get a list of the class
     * packages within in this application
     *
     * @return a list of classes present and that can be used
     */
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
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
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

    /**
     * This sorts the {@link LinkedList<Class>} sorting the OpModes given by a special character
     *
     * @param opModes the OpMode HashMap
     */
    private void sortOpModeMap(HashMap<String, LinkedList<Class<OpMode>>> opModes) {
        Comparator<Class> firstComparator =
                new OpModeComparator();

        for (String key : opModes.keySet()) {
            Collections.sort(opModes.get(key), firstComparator);
        }
    }

    /**
     * Checks a given {@link LinkedList<Class>} for OpModes. All OpModes found are then returned via
     * a HashMap containing the proper groupings.
     *
     * @param klazzes a LinkedList to check for possible OpMode candidates
     * @return a HashMap containing OpMode groupings
     */
    private HashMap<String, LinkedList<Class<OpMode>>> findOpModes(LinkedList<Class> klazzes) {
        Class<TeleOp> teleOpClass = TeleOp.class;
        Class<Autonomous> autoClass = Autonomous.class;
        Class<Disabled> disabledClass = Disabled.class;

        int nextAvailableIndex = 0;
        HashMap<String, LinkedList<Class<OpMode>>> opModes = new HashMap<>();
        for (Class currentClass : klazzes) {
            /*if (!currentClass.isAssignableFrom(OpMode.class)) {
                continue;
            }*/

            if (!currentClass.isAnnotationPresent(disabledClass)) {
                if (currentClass.isAnnotationPresent(teleOpClass)) {
                    Annotation annotation = currentClass.getAnnotation(teleOpClass);
                    String name = ((TeleOp) annotation).pairWithAuto();
                    nextAvailableIndex = getNextAvailableIndex(nextAvailableIndex, opModes, currentClass, name);
                }
                if (currentClass.isAnnotationPresent(autoClass)) {
                    Annotation annotation = currentClass.getAnnotation(autoClass);
                    String name = ((Autonomous) annotation).pairWithTeleOp();
                    nextAvailableIndex = getNextAvailableIndex(nextAvailableIndex, opModes, currentClass, name);
                }
            }
        }
        return opModes;
    }

    /**
     * Gets the next possible grouping in the HashMap and handles it, also handling the incremation
     * of the state index
     */
    private int getNextAvailableIndex(int nextAvailableIndex, HashMap<String, LinkedList<Class<OpMode>>> opModes, Class<OpMode> currentClass, String name) {
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
        return nextAvailableIndex;
    }

    /**
     * Checks the given class against the no-check list, returning {@code true} if the Class is not
     * within the list generated by {@link #buildNoCheckList()}
     *
     * @param klazz the name of the class to check
     * @return {@code true} if the class is not within the no-check-list; otherwise {@code true}
     */
    private boolean shouldAdd(String klazz) {
        // Check against every class name in the no-add list
        for (String noContinue : noCheckList) {
            if (klazz.contains(noContinue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts the OpMode HashMap to a TreeMap, using the proper sorting algorithm.
     *
     * @param opModes the {@code HashMap} containing the properly grouped OpModes
     * @return a {@link TreeMap} with a proper sorting
     */
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

    /**
     * An OpMode injected whenever too many OpModes have been declared, or the size of the
     * characters in the names is greater than the allowable number within the FIRST SDK
     */
    public class TooManyOpModes extends OpMode {
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
