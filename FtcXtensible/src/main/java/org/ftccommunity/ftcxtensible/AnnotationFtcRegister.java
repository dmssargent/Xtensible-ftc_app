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
import com.qualcomm.robotcore.eventloop.opmode.OpModeMeta;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.core.Attachable;
import org.ftccommunity.ftcxtensible.dagger.ReflectionUtilities;
import org.ftccommunity.ftcxtensible.interfaces.FullOpMode;
import org.ftccommunity.ftcxtensible.interfaces.RobotFullOp;
import org.ftccommunity.ftcxtensible.interfaces.RobotInitStartStopLoop;
import org.ftccommunity.ftcxtensible.interfaces.RobotInitStopLoop;
import org.ftccommunity.ftcxtensible.interfaces.RobotLoop;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.opmodes.internal.AttachedOpMode;
import org.ftccommunity.ftcxtensible.opmodes.internal.FtcOpModeFullOpModeRunner;
import org.ftccommunity.ftcxtensible.opmodes.internal.FtcOpModeInterfaceRunner;
import org.ftccommunity.ftcxtensible.opmodes.internal.FtcOpModeRunner;
import org.ftccommunity.ftcxtensible.opmodes.internal.FtcOpModeStopInitInterfaceRunner;
import org.ftccommunity.ftcxtensible.opmodes.internal.OpModeRunnerProvider;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
 * @since 0.2.1
 * @version 0.6.0
 */
public class AnnotationFtcRegister {
    public static final Attachable<AttachedOpMode> OP_MODE_RUNNER_PROVIDER = new OpModeRunnerProvider();
    private static final String TAG = AnnotationFtcRegister.class.getName();
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
        HashMap<String, LinkedList<Class<?>>> opModes = me.findOpModes(classes);
        me.sortOpModeMap(opModes);

        LinkedList<Class<?>> opModesToRegister = new LinkedList<>();
        for (LinkedList<Class<?>> opModeList : me.treeMapify(opModes).values()) {
            for (Class<?> opMode : opModeList) {
                // todo: move this to the appropriate section
                if (opMode.isAnnotationPresent(RobotSdkVersion.class)) {
                    RobotSdkVersion annotation = opMode.getAnnotation(RobotSdkVersion.class);
                    if (annotation.value() != RobotSdkApiLevel.currentVersion()) {
                        if (annotation.strict() ||
                                annotation.compatibleUpTo()
                                        .compareTo(RobotSdkApiLevel.currentVersion()) < 0) {
                            Log.w(TAG, "Skipping OpMode " + getOpModeName(opMode) +
                                    " because of incompatibility");
                            continue;
                        }
                    }
                }

                // todo workaround an the issue of reflectively loading an non-public class

                if (!Modifier.isPublic(opMode.getModifiers())) {
                    RobotLog.setGlobalErrorMsg(getOpModeName(opMode) + " is marked as an OpMode, " +
                            "however it is not public. Please add the keyword 'public' to it");
                }

                opModesToRegister.add(opMode);
            }
        }

        for (Class<?> opMode : opModesToRegister) {
            Log.i(TAG, "Registering " + getOpModeName(opMode));
            final OpModeMeta opModeMeta = generateRegistrationMeta(opMode);
            if (ReflectionUtilities.isParent(opMode, OpMode.class))
                register.register(opModeMeta, (Class) opMode);
            else if (ReflectionUtilities.isParent(opMode, FullOpMode.class))
                register.register(opModeMeta, new FtcOpModeFullOpModeRunner<>((Class<? extends RobotFullOp>) opMode));
            else if (ReflectionUtilities.isParent(opMode, RobotInitStartStopLoop.class))
                register.register(opModeMeta, new FtcOpModeInitStartStopInterfaceRunner<>((Class<? extends RobotInitStartStopLoop>) opMode));
            else if (ReflectionUtilities.isParent(opMode, RobotInitStopLoop.class))
                register.register(opModeMeta, new FtcOpModeStopInitInterfaceRunner<>((Class<? extends RobotInitStopLoop>) opMode));
            else if (ReflectionUtilities.isParent(opMode, RobotLoop.class))
                register.register(opModeMeta, new FtcOpModeInterfaceRunner<>((Class<? extends RobotLoop>) opMode));
            else
                register.register(opModeMeta, new FtcOpModeRunner<>(opMode));
        }

    }

    private static OpModeMeta generateRegistrationMeta(Class<?> opMode) {
        String group;
        boolean teleOp = opMode.isAnnotationPresent(TeleOp.class);
        final String opModeName = getOpModeName(opMode);
        if (teleOp) {
            group = opMode.getAnnotation(TeleOp.class).pairWithAuto();
            return new OpModeMeta(opModeName, OpModeMeta.Flavor.TELEOP, group.equals("") ? OpModeMeta.DefaultGroup : group);
        } else {
            teleOp = opMode.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class);
        }

        if (teleOp) {
            group = opMode.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class).group();
            return new OpModeMeta(opModeName, OpModeMeta.Flavor.TELEOP, group.equals("") ? OpModeMeta.DefaultGroup : group);
        }

        // Repeat for Autonmous
        boolean auto = opMode.isAnnotationPresent(Autonomous.class);
        if (auto) {
            group = opMode.getAnnotation(Autonomous.class).pairWithTeleOp();
            return new OpModeMeta(opModeName, OpModeMeta.Flavor.AUTONOMOUS, group.equals("") ? OpModeMeta.DefaultGroup : group);
        }

        auto = opMode.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class);
        if (auto) {
            group = opMode.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class).group();
            return new OpModeMeta(opModeName, OpModeMeta.Flavor.AUTONOMOUS, group.equals("") ? OpModeMeta.DefaultGroup : group);
        }

        throw new IllegalArgumentException("OpMode does not contain necessary annotations");
    }

    /**
     * This gets the correct name of the OpMode via reflection. This returns the value of the name
     * present in the Annation or Simple Name of the class.
     *
     * @param opMode the OpMode to check
     * @return the name to use of defined by the user
     */
    private static String getOpModeName(Class<?> opMode) {
        String name = "";
        //noinspection deprecation
        if (opMode.isAnnotationPresent(TeleOp.class)) {
            //noinspection deprecation
            name = opMode.getAnnotation(TeleOp.class).name();
        } else if (opMode.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class)) {
            name = opMode.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class).name();
        } else //noinspection deprecation
            if (opMode.isAnnotationPresent(Autonomous.class)) {
                //noinspection deprecation
                name = opMode.getAnnotation(Autonomous.class).name();
            } else if (opMode.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class)) {
                name = opMode.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class).name();
        }

        if (name.equals("")) name = opMode.getSimpleName();

        return name;
    }

    /**
     * Creates a list of packages not to check to improve errors and reduce logging
     */
    private void buildNoCheckList() {
        noCheckList.add("com.google");
        noCheckList.add("io.netty");
        noCheckList.add("com.ftdi");
        noCheckList.add("com.vuforia");
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
    private void sortOpModeMap(HashMap<String, LinkedList<Class<?>>> opModes) {
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
    private HashMap<String, LinkedList<Class<?>>> findOpModes(LinkedList<Class> klazzes) {
        @SuppressWarnings("deprecation") Class<TeleOp> teleOpClass = TeleOp.class;
        @SuppressWarnings("deprecation") Class<Autonomous> autoClass = Autonomous.class;
        @SuppressWarnings("deprecation") Class<Disabled> disabledClass = Disabled.class;

        int nextAvailableIndex = 0;
        HashMap<String, LinkedList<Class<?>>> opModes = new HashMap<>();
        for (Class currentClass : klazzes) {
            /*if (!currentClass.isAssignableFrom(OpMode.class)) {
                continue;
            }*/

            final boolean isNotDisabled = !(currentClass.isAnnotationPresent(disabledClass) || currentClass.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Disabled.class));
            if (isNotDisabled) {
                if (currentClass.isAnnotationPresent(teleOpClass)) {
                    Annotation annotation = currentClass.getAnnotation(teleOpClass);
                    @SuppressWarnings("deprecation") String name = ((TeleOp) annotation).pairWithAuto();
                    nextAvailableIndex = getNextAvailableIndex(nextAvailableIndex, opModes, currentClass, name);
                } else if (currentClass.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class)) {
                    Annotation annotation = currentClass.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class);
                    String name = ((com.qualcomm.robotcore.eventloop.opmode.TeleOp) annotation).group();
                    nextAvailableIndex = getNextAvailableIndex(nextAvailableIndex, opModes, currentClass, name);
                }
                if (currentClass.isAnnotationPresent(autoClass)) {
                    Annotation annotation = currentClass.getAnnotation(autoClass);
                    //noinspection deprecation
                    String name = ((Autonomous) annotation).pairWithTeleOp();
                    nextAvailableIndex = getNextAvailableIndex(nextAvailableIndex, opModes, currentClass, name);
                }
                if (currentClass.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class)) {
                    Annotation annotation = currentClass.getAnnotation(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class);
                    String name = ((com.qualcomm.robotcore.eventloop.opmode.Autonomous) annotation).group();
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
    private int getNextAvailableIndex(int nextAvailableIndex, HashMap<String, LinkedList<Class<?>>> opModes, Class<?> currentClass, String name) {
        if (name.equals("")) {
            int i = ++nextAvailableIndex;
            LinkedList<Class<?>> temp = new LinkedList<>();
            temp.add(currentClass);
            opModes.put(Integer.toString(i), temp);
        } else {
            if (opModes.containsKey(name)) {
                opModes.get(name).add(currentClass);
            } else {
                LinkedList<Class<?>> temp = new LinkedList<>();
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
    private TreeMap<String, LinkedList<Class<?>>> treeMapify(
            HashMap<String, LinkedList<Class<?>>> opModes) {

        TreeMap<String, LinkedList<Class<?>>> sortedOpModes = new TreeMap<>();
        for (String key : opModes.keySet()) {
            Class<?> opMode = opModes.get(key).getFirst();
            String name = getOpModeName(opMode);

            sortedOpModes.put(name, opModes.get(key));
        }

        return sortedOpModes;
    }

    private static class OpModeComparator implements Comparator<Class> {
        @Override
        public int compare(Class lhs, Class rhs) {
            @SuppressWarnings("deprecation") final boolean leftHandSideTeleOp = lhs.isAnnotationPresent(TeleOp.class) || lhs.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class);
            @SuppressWarnings("deprecation") final boolean leftHandSideAutonomous = lhs.isAnnotationPresent(Autonomous.class) || lhs.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class);
            @SuppressWarnings("deprecation") final boolean rightHandSideTeleOp = rhs.isAnnotationPresent(TeleOp.class) || rhs.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.TeleOp.class);
            @SuppressWarnings("deprecation") final boolean rightHandSideAutonomous = rhs.isAnnotationPresent(Autonomous.class) || rhs.isAnnotationPresent(com.qualcomm.robotcore.eventloop.opmode.Autonomous.class);
            if (leftHandSideTeleOp && rightHandSideTeleOp) {
                return getOpModeName(lhs).compareTo(getOpModeName(rhs));
            }

            if (leftHandSideAutonomous && rightHandSideTeleOp) {
                return 1;
            } else if (leftHandSideTeleOp && rightHandSideAutonomous) {
                return -1;
            }

            if (leftHandSideAutonomous && rightHandSideAutonomous) {
                return getOpModeName(lhs).compareTo(getOpModeName(rhs));
            }

            return -1;
        }
    }
}
