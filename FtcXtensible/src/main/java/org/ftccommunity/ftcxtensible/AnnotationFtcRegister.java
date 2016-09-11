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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.core.Attachable;
import org.ftccommunity.ftcxtensible.dagger.ReflectionUtilities;
import org.ftccommunity.ftcxtensible.dagger.SimpleDag;
import org.ftccommunity.ftcxtensible.dagger.annonations.Named;
import org.ftccommunity.ftcxtensible.dagger.annonations.Provides;
import org.ftccommunity.ftcxtensible.dagger.annonations.VariableNamedProvider;
import org.ftccommunity.ftcxtensible.interfaces.RobotInitStartStopLoop;
import org.ftccommunity.ftcxtensible.interfaces.RobotInitStopLoop;
import org.ftccommunity.ftcxtensible.interfaces.RobotLoop;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.CustomRunner;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.opmodes.methods.EntryPoint;
import org.ftccommunity.ftcxtensible.opmodes.methods.Init;
import org.ftccommunity.ftcxtensible.opmodes.methods.Loop;
import org.ftccommunity.ftcxtensible.opmodes.methods.Stop;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

import static com.google.common.base.Preconditions.checkState;

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
                // fails silenty
                if (!Modifier.isPublic(opMode.getModifiers())) {
                    RobotLog.setGlobalErrorMsg(getOpModeName(opMode) + " is marked as an OpMode" +
                            ", however it is not public. Please add the keyword 'public' to it");
                }
                // register.register(getOpModeName(opMode), opMode);
                opModesToRegister.add(opMode);
            }
        }

        for (Class<?> opMode : opModesToRegister) {
            Log.i(TAG, "Registering " + getOpModeName(opMode));
            if (ReflectionUtilities.isParent(opMode, OpMode.class))
                register.register(getOpModeName(opMode), (Class<? extends OpMode>) opMode);
            else if (ReflectionUtilities.isParent(opMode, RobotInitStartStopLoop.class))
                register.register(getOpModeName(opMode), new FtcOpModeInitStartStopInterfaceRunner<>((Class<? extends RobotInitStartStopLoop>) opMode));
            else if (ReflectionUtilities.isParent(opMode, RobotInitStopLoop.class))
                register.register(getOpModeName(opMode), new FtcOpModeStopInitInterfaceRunner<>((Class<? extends RobotInitStopLoop>) opMode));
            else if (ReflectionUtilities.isParent(opMode, RobotLoop.class))
                register.register(getOpModeName(opMode), new FtcOpModeInterfaceRunner<>((Class<? extends RobotLoop>) opMode));
            else register.register(getOpModeName(opMode), new FtcOpModeRunner<>(opMode));
        }

    }

    /**
     * This gets the correct name of the OpMode via reflection. This returns the value of the name
     * present in the Annation or Simple Name of the class.
     *
     * @param opMode the OpMode to check
     * @return the name to use of defined by the user
     */
    private static String getOpModeName(Class<?> opMode) {
        String name;
        if (opMode.isAnnotationPresent(TeleOp.class)) {
            name = opMode.getAnnotation(TeleOp.class).name();
        } else if (opMode.isAnnotationPresent(Autonomous.class)) {
            name = opMode.getAnnotation(Autonomous.class).name();
        } else {
            name = opMode.getSimpleName();
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
        Class<TeleOp> teleOpClass = TeleOp.class;
        Class<Autonomous> autoClass = Autonomous.class;
        Class<Disabled> disabledClass = Disabled.class;

        int nextAvailableIndex = 0;
        HashMap<String, LinkedList<Class<?>>> opModes = new HashMap<>();
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

    public static class FtcOpModeRunner<T> extends AttachedOpMode {
        private final Class<T> opClass;
        private final OpModeType type;
        private final Method mainMethod;
        @Nullable
        private final Method initMethod;
        @Nullable
        private final Method stopMethod;
        private Object instance;

        public FtcOpModeRunner(Class<T> opClass) {
            this.opClass = opClass;

            OpModeTypes temp = null;
            if (opClass.isAnnotationPresent(CustomRunner.class)) {
                final Class<? extends OpModeType> value = opClass.getAnnotation(CustomRunner.class).value();
                type = SimpleDag.create(value, null);
            } else {
                for (OpModeTypes opModeType : OpModeTypes.values()) {
                    if (opModeType.check(opClass)) {
                        if (temp == null) {
                            temp = opModeType;
                        } else if ((temp == OpModeTypes.LOOP && opModeType == OpModeTypes.FULL) ||
                                (temp == OpModeTypes.FULL && opModeType == OpModeTypes.LOOP)) {
                            temp = OpModeTypes.FULL;
                        } else {
                            throw new UnknownError("Cannot determine type, the class \"" +
                                    opClass.getSimpleName() + "\" fits the contract for \"" +
                                    temp + "\" and \"" + opModeType + "\"");
                        }
                    }
                }
                if (temp == null) {
                    throw new UnknownError("Cannot determine type, the class \"" +
                            opClass.getSimpleName() + "\" fits no available contracts.");
                }
                type = temp;
            }

            initMethod = type.initMethod(opClass);
            mainMethod = type.mainMethod(opClass);
            stopMethod = type.stopMethod(opClass);
        }

        @Override
        public void init() {
            super.init();
            recreateInstance();
            type.init(initMethod, instance);
        }

        @Override
        public void loop() {
            super.loop();
            type.loop(mainMethod, instance);
        }

        @Override
        public void stop() {
            type.stop(stopMethod, instance);
            instance = null;
            super.stop();
        }

        private void recreateInstance() {
            instance = SimpleDag.create(opClass, AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER);
        }

        private enum OpModeTypes implements OpModeType {
            MAIN {
                @Override
                public void init(Method method, Object instance) {
                    try {
                        method.invoke(null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void loop(Method method, Object instance) {

                }

                @Override
                public void stop(Method method, Object instance) {

                }

                @Override
                public Method initMethod(Class<?> instance) {
                    return mainMethod(instance);
                }

                @NonNull
                @Override
                public Method mainMethod(Class<?> instance) {
                    try {
                        return instance.getMethod("main");
                    } catch (NoSuchMethodException e) {
                        throw verify(MAIN, instance);
                    }
                }

                @Override
                public Method stopMethod(Class<?> instance) {
                    return null;
                }

                @Override
                public boolean check(Class<?> klazz) {
                    for (Method method : klazz.getMethods()) {
                        if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("main")) {
                            return true;
                        }
                    }

                    return false;
                }
            }, ENTRY_POINT {
                @Override
                public void init(Method method, Object instance) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void loop(Method method, Object instance) {

                }

                @Override
                public void stop(Method method, Object instance) {

                }

                @Override
                public Method initMethod(Class<?> instance) {
                    return mainMethod(instance);
                }

                @Override
                @NonNull
                public Method mainMethod(Class<?> instance) {
                    for (Method method : instance.getMethods()) {
                        if (method.isAnnotationPresent(EntryPoint.class)) {
                            return method;
                        }
                    }

                    throw verify(ENTRY_POINT, instance);
                }

                @Override
                public Method stopMethod(Class<?> instance) {
                    return null;
                }

                @Override
                public boolean check(Class<?> klazz) {
                    boolean entryPointDetected = false;
                    for (Method method : klazz.getMethods()) {
                        if (method.isAnnotationPresent(EntryPoint.class)) {
                            if (entryPointDetected) {
                                return false;
                            }
                            entryPointDetected = true;
                        }
                    }

                    return entryPointDetected;
                }
            }, LOOP {
                @Override
                public void init(Method method, Object instance) {

                }

                @Override
                public void loop(Method method, Object instance) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void stop(Method method, Object instance) {

                }

                @Override
                public Method initMethod(Class<?> instance) {
                    return null;
                }

                @Override
                @NonNull
                public Method mainMethod(Class<?> instance) {
                    for (Method method : instance.getMethods()) {
                        if (method.isAnnotationPresent(Loop.class)) {
                            return method;
                        }
                    }

                    throw verify(LOOP, instance);
                }

                @Override
                public Method stopMethod(Class<?> instance) {
                    return null;
                }

                @Override
                public boolean check(Class<?> klazz) {
                    boolean entryPointDetected = false;
                    for (Method method : klazz.getMethods()) {
                        if (method.isAnnotationPresent(Loop.class)) {
                            if (entryPointDetected) {
                                return false;
                            }
                            entryPointDetected = true;
                        }
                    }

                    return entryPointDetected;
                }
            }, FULL {
                @Override
                public void init(Method method, Object instance) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void loop(Method method, Object instance) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void stop(Method method, Object instance) {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Method initMethod(Class<?> instance) {
                    for (Method method : instance.getMethods()) {
                        if (method.isAnnotationPresent(Init.class)) {
                            return method;
                        }
                    }

                    return null;
                }

                @Override
                @NonNull
                public Method mainMethod(Class<?> instance) {
                    for (Method method : instance.getMethods()) {
                        if (method.isAnnotationPresent(Loop.class)) {
                            return method;
                        }
                    }

                    throw verify(FULL, instance);
                }

                @Override
                public Method stopMethod(Class<?> instance) {
                    for (Method method : instance.getMethods()) {
                        if (method.isAnnotationPresent(Stop.class)) {
                            return method;
                        }
                    }

                    return null;
                }

                @Override
                public boolean check(Class<?> klazz) {
                    if (!LOOP.check(klazz)) {
                        return false;
                    }

                    boolean initPointDetected = false;
                    boolean stopPointDetected = false;
                    for (Method method : klazz.getMethods()) {
                        if (method.isAnnotationPresent(Init.class)) {
                            if (initPointDetected) {
                                return false;
                            }
                            initPointDetected = true;
                        }
                        if (method.isAnnotationPresent(Stop.class)) {
                            if (stopPointDetected) {
                                return false;
                            }
                            stopPointDetected = true;
                        }
                    }

                    return initPointDetected || stopPointDetected;
                }
            };

            private static RuntimeException verify(OpModeType type, Class<?> instance) {
                if (type.check(instance)) {
                    return new RuntimeException("Contract passed, missing method! impl bad");
                } else {
                    return new UnsupportedOperationException("Class is not qualified for the operation");
                }
            }
        }
    }

    public static class FtcOpModeInterfaceRunner<T extends RobotLoop> extends AttachedOpMode {
        private final Class<T> loop;
        private T currentInstance;


        public FtcOpModeInterfaceRunner(Class<T> loop) {
            this.loop = loop;

        }

        @Override
        @CallSuper
        public void init() {
            super.init();
            recreateDelegate();
        }

        @Override
        @CallSuper
        public void loop() {
            super.loop();
            currentInstance.loop();
        }

        @Override
        @CallSuper
        public void stop() {
            currentInstance = null;
            super.stop();
        }

        @Contract(pure = true)
        @NotNull
        protected T delegate() {
            if (currentInstance == null) {
                recreateDelegate();
            }

            return currentInstance;

        }

        private void recreateDelegate() {
            if (currentInstance == null) {
                currentInstance = SimpleDag.create(loop, AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER);
            }
        }
    }

    public static class FtcOpModeStopInitInterfaceRunner<T extends RobotInitStopLoop> extends FtcOpModeInterfaceRunner<T> {
        public FtcOpModeStopInitInterfaceRunner(Class<T> loop) {
            super(loop);
        }

        @Override
        public void init() {
            super.init();
            delegate().init();
        }

        @Override
        public void stop() {
            delegate().stop();
            super.stop();
        }
    }

    public static class FtcOpModeInitStartStopInterfaceRunner<T extends RobotInitStartStopLoop> extends FtcOpModeStopInitInterfaceRunner<T> {
        public FtcOpModeInitStartStopInterfaceRunner(Class<T> loop) {
            super(loop);
        }

        @Override
        public void start() {
            delegate().start();
        }
    }

    public static class AttachedOpMode extends OpMode {
        @Override
        @CallSuper
        public void init() {
            AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER.attach(this);
        }

        @Override
        public void loop() {

        }

        @CallSuper
        @Override
        public void stop() {
            super.stop();
            AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER.detach();
        }
    }

    private static class OpModeRunnerProvider implements Attachable<AttachedOpMode> {
        private AttachedOpMode coreProvider = null;
        private ExtensibleHardwareMap extensibleHardwareMap = null;

        @Override
        @Nullable
        public AttachedOpMode attach(@NotNull AttachedOpMode provider) {
            AttachedOpMode temp = detach();
            coreProvider = provider;
            extensibleHardwareMap = new ExtensibleHardwareMap(coreProvider.hardwareMap);
            return temp;
        }

        @Override
        @Nullable
        public AttachedOpMode detach() {
            AttachedOpMode temp = coreProvider;
            coreProvider = null;
            extensibleHardwareMap = null;
            return temp;
        }

        @Override
        @Contract(pure = true)
        public boolean isAttached() {
            return coreProvider != null;
        }

        @Provides
        public Telemetry providesTelemetry() {
            checkState(isAttached(), "provider is not attached");
            return coreProvider.telemetry;
        }

        @Provides
        public Gamepad providesGamepad() {
            checkState(isAttached(), "provider is not attached");
            return coreProvider.gamepad1;
        }

        @Provides
        @Named("gamepad2")
        public Gamepad providesGamepad2() {
            checkState(isAttached(), "provider is not attached");
            return coreProvider.gamepad2;
        }

        @Provides
        @Named("gamepad1")
        public Gamepad providesGamepad1() {
            checkState(isAttached(), "provider is not attached");
            return coreProvider.gamepad1;
        }

        @Provides
        public HardwareMap providesHardwareMap() {
            checkState(isAttached(), "provider is not attached");
            return coreProvider.hardwareMap;
        }

        @Provides
        @VariableNamedProvider
        public HardwareDevice providesHardwareDevices(String name) {
            checkState(isAttached(), "provider is not attached");
            return extensibleHardwareMap.get(name);
        }
    }
}
