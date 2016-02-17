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
package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.common.base.Throwables;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.hardware.camera.ExtensibleCameraManager;
import org.ftccommunity.ftcxtensible.interfaces.AbstractRobotContext;
import org.ftccommunity.ftcxtensible.interfaces.FullOpMode;
import org.ftccommunity.ftcxtensible.interfaces.RunAssistant;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.ftccommunity.ftcxtensible.opmodes.RobotsDontQuit;
import org.ftccommunity.ftcxtensible.robot.handlers.RobotUncaughtExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;

/**
 * The main Xtensible OpMode, any desirable OpMode that this library presents has at one point
 * devired from this class. This bootstraps the majority of the OpMode processing done by the
 * Xtensible library
 *
 * @author David Sargent - FTC5395
 * @since 0.1
 */
@NotDocumentedWell
public abstract class ExtensibleOpMode extends OpMode implements FullOpMode, AbstractRobotContext {
    public static final String TAG = "XTENSIBLE_OP_MODE::";
    private transient static ExtensibleOpMode parent;

    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;
    private final RobotContext robotContext;
    private ExtensibleLoopManager loopManager;
    private int loopCount;
    private volatile int skipNextLoop;

    private boolean logTimes;
    private EvictingQueue<Integer> loopTimes;
    private VariableTracer tracer;

    private boolean isStopped = false;

    /**
     * Bootstraps the Extensible OpMode to the Xtensible library
     */
    protected ExtensibleOpMode() {
        this.gamepad1 = super.gamepad1;
        this.gamepad2 = super.gamepad2;
        this.hardwareMap = super.hardwareMap;

//        if (super.hardwareMap.appContext == null) {
//            RobotLog.w("App Context is null during construction.");
//        }

        this.telemetry = super.telemetry;
        loopCount = 0;
        skipNextLoop = 0;

        if (parent == null) {
            robotContext = new RobotContext(hardwareMap, telemetry);
            parent = this;
        } else {
            robotContext = parent.robotContext;
        }

        loopManager = new ExtensibleLoopManager();
        loopTimes = EvictingQueue.create(50);

        Log.i(TAG, "OpMode: " + this.getClass().getSimpleName());
    }

    /**
     * Creates a new Extensible OpMode, with the master being the given parent
     *
     * @param prt the parent Extensible OpMode
     */
    protected ExtensibleOpMode(ExtensibleOpMode prt) {
        this();
        parent = prt;
    }

    /**
     * Initialize the Extensible OpMode and perform the user code operations to initialize the
     * robot
     */
    @Override
    public final void init() {
        if (super.gamepad1 == null) {
            super.gamepad1 = new Gamepad();
        }

        if (super.gamepad2 == null) {
            super.gamepad2 = new Gamepad();
        }

        prepare(super.hardwareMap.appContext, super.hardwareMap, super.gamepad1, super.gamepad2);

        // Upgrade thread priority
        Thread.currentThread().setPriority(7);

        // Build an exception handler
        robotContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Activity controller = (Activity) robotContext.appContext();
                @SuppressWarnings("ResourceType") PendingIntent intent = PendingIntent.getActivity(controller.getBaseContext(), 0,
                        new Intent(controller.getIntent()), controller.getIntent().getFlags());
                Thread.currentThread().setUncaughtExceptionHandler(new RobotUncaughtExceptionHandler(robotContext.appContext(), intent, 250));
            }
        });

        LinkedList<Object> list = new LinkedList<>();
        try {
            init(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            handleException(list, e);
        }

        postProcess(list, RobotStatus.MainStates.START);
    }


    /**
     * Starts the user code operations to start the robot
     */
    @Override
    public final void start() {
        robotContext.status().setMainState(RobotStatus.MainStates.START);
        LinkedList<Object> list = new LinkedList<>();
        try {
            start(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            handleException(list, e);
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, robotContext.status().getMainRobotState(), null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, robotContext.status().getMainRobotState(), o);
            }
        }

        telemetry().sendData();
    }


    /**
     * The looping system of Extensible OpMode
     */
    @Override
    public final void loop() {
        loopCount++;
        if (skipNextLoop > 0) {
            skipNextLoop--;
            Log.i(TAG, "Skipping Loop #" + getLoopCount());
            return;
        }

        //  determine if the following line is needed;  I think not
        // bindHardwareMap(super.hardwareMap);
        if (robotContext.status().getMainRobotState() == RobotStatus.MainStates.EXCEPTION &&
                (robotContext.status().getCurrentStateType() == RobotStatus.Type.FAILURE ||
                        robotContext.status().getCurrentStateType() == RobotStatus.Type.IDK)) {
            throw new IllegalStateException("Robot cannot continue to execute, due to an exception");
        }

        // Good to continue
        long startTime = System.nanoTime();

        // Update the gamepads
        gamepad1().updateGamepad(robotContext, super.gamepad1);
        gamepad2().updateGamepad(robotContext, super.gamepad2);

        // Pre loop init
        robotContext.status().setMainState(RobotStatus.MainStates.EXEC);
        LinkedList<Object> list = new LinkedList<>();

        // Start loop checks
        for (RunAssistant assistant : loopManager.getPreloopAssistants(getLoopCount())) {
            runAssistant(assistant);
        }

        // Main loop
        try {
            loop(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            handleException(list, e);
        }

        // Post loop processing
        for (RunAssistant assistant : loopManager.getPostLoopAssistants(getLoopCount())) {
            runAssistant(assistant);
        }

        postProcess(list, robotContext.status().getMainRobotState());

        // Get the delta time and check if it was longer than 50ms
        long endTime = System.nanoTime();
        long timeTaken = (endTime - startTime);
        if (timeTaken - (1000000 * 50) > 0) {
            Log.w(TAG, "User code took long than " + 50 + "ms. Time: " +
                    timeTaken + "ms");
        }

        if (logTimes) {
            loopTimes.add((int) timeTaken);
            tracer.log();
        }
    }

    /**
     * Stop the robot
     */
    @Override
    public final void stop() {
        if (!isStopped) {
            LinkedList<Object> list = new LinkedList<>();
            try {
                stop(robotContext, list);
            } catch (InterruptedException ex) {
                return;
            } catch (Exception e) {
                handleException(list, e);
            }

            postProcess(list, RobotStatus.MainStates.STOP);

            if (logTimes) {
                File perfFile = new File(Environment.getExternalStorageDirectory() + "/perf_" + System.currentTimeMillis() + ".json");
                Log.i(TAG, "Saving Loop Performance File at " + perfFile);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try {
                    FileWriter outputStream = new FileWriter(perfFile);
                    outputStream.write(gson.toJson(new PerformanceTuner((Integer[]) loopTimes.toArray())));
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }

            parent = null;
            release();
            isStopped = true;
        }
    }

    /**
     * Post process the given details of the user code segment
     *
     * @param list  the list as generated by the user code
     * @param state the {@link RobotStatus} reflecting the status of the current robot
     */
    private void postProcess(LinkedList<Object> list, RobotStatus.MainStates state) {
        if (list.isEmpty()) {
            onSuccess(robotContext, state, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, state, o);
            }
        }

        telemetry().sendData();
    }

    /**
     * Gets how many loop cycles have completed, including the current cycle
     *
     * @return the number of complete loop cycles, including the current one
     */
    protected final int getLoopCount() {
        return loopCount;
    }

    /**
     * Tells the loop manager to skip the next loop cycle, this is additive, so calling this
     * function twice skips two loops
     */
    protected final void skipNextLoop() {
        skipNextLoop++;
    }

    /**
     * Enables the current OpMode that devires this to be recorded for debugging reasons
     *
     * @param child the OpMode that needs to be monitored
     * @param <T>   the type of the monitored child
     */
    protected final <T> void enableLoopPerformanceCapture(@NotNull T child) {
        this.tracer = new VariableTracer<>(child);
        logTimes = true;
    }

    /**
     * Disables loop capture
     */
    protected final void disableLoopPerformanceCapture() {
        logTimes = false;
    }

    /**
     * Handles an user code exception
     *
     * @param list the list of objects to process
     * @param e    the exception thrown by user code
     */
    private void handleException(LinkedList<Object> list, Exception e) {
        Log.e(TAG,
                "An exception occurred running the OpMode " + getCallerClassName(e), e);
        if (e instanceof NullPointerException && (e.getMessage() == null || e.getMessage().equals(""))) {
            String className = "";
            String lineNumber = "";
            if (e.getStackTrace().length > 0 && e.getStackTrace()[0] != null) {
                className = e.getStackTrace()[0].getClassName();
                lineNumber = Integer.toString(e.getStackTrace()[0].getLineNumber());
            }

            e = new NullPointerException("Something went awry in the OpMode, because something" +
                    " seems to be null." +
                    (lineNumber.equals("") && className.equals("") ?
                            "" : " Check the line " + lineNumber + "@" + className + " for possible errors."));
        }

        robotContext.status().setCurrentStateType(RobotStatus.Type.IDK);
        RobotStatus.Type failure = robotContext.status().getCurrentStateType();
        if (onFailure(robotContext, failure, RobotStatus.MainStates.EXCEPTION, e) >= 0) {
            robotContext.status().setCurrentStateType(RobotStatus.Type.SUCCESS);
            for (Object o : list) {
                onFailure(robotContext, failure, RobotStatus.MainStates.EXCEPTION, o);
            }
        } else {
            if (this.getClass().isAnnotationPresent(RobotsDontQuit.class)) {
                final Exception ex = e;
                Thread codeRestarter = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        skipNextLoop = Integer.MAX_VALUE;
                        final Gamepad gamepad1 = ExtensibleOpMode.super.gamepad1;
                        final Gamepad gamepad2 = ExtensibleOpMode.super.gamepad2;
                        RobotLog.setGlobalErrorMsg(ex.toString() + "\n Robot Code will restart automatically in 5 seconds");
                        final OpModeManager mgr = robotContext.opModeManager();
                        final String currentName = mgr.getActiveOpModeName();
                        mgr.stopActiveOpMode();
                        try {
                            for (int i = 5; i >= 0; i--) {
                                RobotLog.clearGlobalErrorMsg();
                                RobotLog.setGlobalErrorMsg(ex.toString() + "\n Robot Code will restart automatically in " + i + " seconds");
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        RobotLog.clearGlobalErrorMsg();
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        final RobotContext context = robotContext;
//                        try {
//                            Method robotRestart = context.appContext().getClass().getMethod("requestRobotRestart");
//                            robotRestart.setAccessible(true);
//                            robotRestart.invoke(context.appContext());
//                            Thread.sleep(10000);
//                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InterruptedException e1) {
//                            e1.printStackTrace();
//                        }
                        try {
                            do {
                                Log.i(TAG, "Attempted to start " + currentName);
                                RobotLog.setGlobalErrorMsg("Attempting to start " + currentName);
                                Thread.sleep(20);
                                RobotLog.clearGlobalErrorMsg();
                                Thread.sleep(20);
                                mgr.initActiveOpMode(currentName);
                                mgr.startActiveOpMode();
                                mgr.runActiveOpMode(new Gamepad[]{gamepad1, gamepad2});
                            } while (!mgr.getActiveOpModeName().equals(currentName));
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                codeRestarter.setName(robotContext.opModeManager().getActiveOpModeName() + " Restarter");
                codeRestarter.start();
            } else {
                robotContext.status().setCurrentStateType(RobotStatus.Type.FAILURE);
                RobotLog.setGlobalErrorMsg(e.toString());
                Throwables.propagate(e);
            }
        }
    }

    private String getCallerClassName(Exception e) {
        StackTraceElement[] stElements = e.getStackTrace();
        for (StackTraceElement ste : stElements) {
            if (!ste.getClassName().equals(ExtensibleOpMode.class.getName()) &&
                    ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getMethodName() + ":" + ste.getLineNumber() + "@" + ste.getClassName();
            }
        }

        return "";
    }

    /**
     * Returns the current {@link RobotContext}
     *
     * @return the master {@code RobotContext}
     */
    protected final RobotContext context() {
        return robotContext;
    }

    @NotNull
    public ExtensibleLoopManager loopManager() {
        return loopManager;
    }

    @Override
    public RobotContext enableNetworking() {
        return robotContext.enableNetworking();
    }

    @Override
    public RobotContext disableNetworking() {
        return robotContext.disableNetworking();
    }

    @Override
    public void bindHardwareMap(@NotNull HardwareMap hwMap) {
        context().bindHardwareMap(hwMap);
    }

    @Override
    public void rebuildHardwareMap() {
        context().rebuildHardwareMap();
    }

    @Override
    @Deprecated
    public HardwareMap legacyHardwareMap() {
        return robotContext.legacyHardwareMap();
    }

    @Override
    public RobotContext startNetworking() {
        return robotContext.startNetworking();
    }

    @Override
    public RobotContext stopNetworking() {
        return robotContext.stopNetworking();
    }

    public ExtensibleHardwareMap hardwareMap() {
        return robotContext.hardwareMap();
    }

    @Override
    public void bindAppContext(Context context) throws IllegalArgumentException, IllegalStateException {
        robotContext.bindAppContext(context);
    }

    @Override
    public void prepare(Context ctx, HardwareMap hwMap, Gamepad gamepad1, Gamepad gamepad2) {
        robotContext.prepare(ctx, hwMap, gamepad1, gamepad2);
    }

    @Override
    public Context appContext() {
        return robotContext.appContext();
    }

    @Deprecated
    @Override
    public Gamepad legacyGamepad1() {
        return robotContext.legacyGamepad1();
    }

    @Deprecated
    @Override
    public Gamepad legacyGamepad2() {
        return robotContext.legacyGamepad2();
    }

    @Override
    public ServerSettings serverSettings() {
        return robotContext.serverSettings();
    }

    @Override
    public RobotLogger log() {
        return robotContext.log();
    }

    @Override
    public void submitAsyncTask(Runnable runnable) {
        robotContext.submitAsyncTask(runnable);
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        robotContext.runOnUiThread(runnable);
    }

    @Override
    public RobotStatus status() {
        return robotContext.status();
    }

    @Override
    public void addPostData(Collection<InterfaceHttpData> datas) {
        robotContext.addPostData(datas);
    }

    @Override
    public ImmutableList<InterfaceHttpData> getPostedData() {
        return robotContext.getPostedData();
    }

    public ExtensibleTelemetry telemetry() {
        return robotContext.telemetry();
    }

    public ExtensibleGamepad gamepad1() {
        return robotContext.gamepad1();
    }

    public ExtensibleGamepad gamepad2() {
        return robotContext.gamepad2();
    }

    @Override
    public ExtensibleCameraManager cameraManager() {
        return robotContext.cameraManager();
    }

    @Override
    public void release() {
        robotContext.release();
    }

    @NotNull
    @Override
    public DataBinder controllerBindings() {
        return robotContext.controllerBindings();
    }

    @NotNull
    @Override
    public View robotControllerView() {
        return robotContext.robotControllerView();
    }

    @Override
    @NotNull
    public OpModeManager opModeManager() {
        return robotContext.opModeManager();
    }

    private void runAssistant(RunAssistant assistant) {
        LinkedList<Object> list = new LinkedList<>();
        try {
            assistant.onExecute(robotContext, list);
        } catch (InterruptedException ex) {
            handleInterrupt(ex);
            return;
        } catch (Exception e) {
            handleException(list, e);
        }

        postProcess(list, robotContext.status().getMainRobotState());
    }

    private void handleInterrupt(InterruptedException ex) {
        if (!Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
        }
        Throwables.propagate(ex);
    }

    private class VariableTrace {
        private String name;
        private Object value;

        public VariableTrace(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    class VariableTracer<T> {
        private transient T current;
        private LinkedList<VariableTrace> traces;

        public VariableTracer(T o) {
            current = o;
            traces = new LinkedList<>();
        }

        public void log() {
            LinkedList<VariableTrace> fields = new LinkedList<>();
            for (Field field : current.getClass().getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers())) {
                    try {
                        fields.add(new VariableTrace(field.getName(), field.get(current)));
                    } catch (IllegalAccessException | ClassCastException e) {
                        Log.e(TAG, e.getLocalizedMessage(), e);
                    }
                }
            }

            traces.addAll(fields);
        }

        public VariableTrace[] get() {
            return (VariableTrace[]) traces.toArray();
        }
    }
}
