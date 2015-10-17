/*
 *
 *  * Copyright © 2015 David Sargent
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  * and associated documentation files (the “Software”), to deal in the Software without restriction,
 *  * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  * do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all copies or
 *  * substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ftccommunity.ftcxtensible.robot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.interfaces.FullOpMode;
import org.ftccommunity.ftcxtensible.interfaces.OpModeLoop;
import org.ftccommunity.ftcxtensible.interfaces.RunAssistant;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.handlers.RobotUncaughtExceptionHandler;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author David Sargent - FTC5395
 * @since 0.1
 */
@Alpha
@NotDocumentedWell
public abstract class ExtensibleOpMode extends OpMode implements FullOpMode {
    public static final String TAG = "XTENSTIBLE_OP_MODE::";
    private transient static ExtensibleOpMode parent;

    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;

    private final LinkedHashMap<Integer, LinkedList<RunAssistant>> beforeXLoop;
    private final TreeMap<Integer, OpModeLoop> loops;

    private TreeMap<Integer, LinkedList<RunAssistant>> beforeEveryXLoop;
    private TreeMap<Integer, LinkedList<RunAssistant>> afterEveryXLoop;
    private LinkedHashMap<Integer, LinkedList<RunAssistant>> afterXLoop;

    private RobotContext robotContext;
    private int loopCount;
    private volatile int skipNextLoop;

    protected ExtensibleOpMode() {
        this.gamepad1 = super.gamepad1;
        this.gamepad2 = super.gamepad2;
        this.hardwareMap = super.hardwareMap;
        if (super.hardwareMap.appContext == null) {
            RobotLog.w("App Context is null during construction.");
        }

        this.telemetry = super.telemetry;
        loopCount = 0;
        skipNextLoop = 0;

        if (parent == null) {
            robotContext = new RobotContext(gamepad1, gamepad2, hardwareMap, telemetry);
            parent = this;
        } else {
            robotContext = parent.robotContext;
        }

        loops = new TreeMap<>();
        beforeXLoop = new LinkedHashMap<>();
        beforeEveryXLoop = new TreeMap<>();
        afterEveryXLoop = new TreeMap<>();
        afterXLoop = new LinkedHashMap<>();
    }

    protected ExtensibleOpMode(ExtensibleOpMode prt) {
        this();
        parent = prt;
    }

    @Override
    public final void init() {
        robotContext.prepare(super.hardwareMap.appContext);

        // Upgrade thread priority
        Thread.currentThread().setPriority(7);

        // Build an exception handler
        robotContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().setUncaughtExceptionHandler(UncaughtExceptionHandlers.systemExit());
                Activity controller = (Activity) robotContext.getAppContext();
                @SuppressWarnings("ResourceType") PendingIntent intent = PendingIntent.getActivity(controller.getBaseContext(), 0,
                        new Intent(controller.getIntent()), controller.getIntent().getFlags());
                Thread.currentThread().setUncaughtExceptionHandler(new RobotUncaughtExceptionHandler(robotContext.getAppContext(), intent));
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
        telemetry().sendData();
    }


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


    @Override
    public final void loop() {
        loopCount++;
        if (skipNextLoop > 0) {
            skipNextLoop--;
            Log.i(TAG, "Skipping Loop #" + getLoopCount());
            return;
        }

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
        if (beforeXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : beforeXLoop.get(loopCount)) {
                runAssistant(assistant);
            }
        }

        if (beforeEveryXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : beforeEveryXLoop.get(loopCount)) {
                runAssistant(assistant);
            }
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
        if (afterEveryXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : afterEveryXLoop.get(loopCount)) {
                runAssistant(assistant);
            }
        }

        if (afterXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : afterXLoop.get(loopCount)) {
                runAssistant(assistant);
            }
        }

        postProcess(list, robotContext.status().getMainRobotState());

        // Get the delta time and check if it was longer than 50ms
        long endTime = System.nanoTime();
        if ((endTime - startTime) - (1000000 * 50) > 0) {
            Log.w(TAG, "User code took long than " + 50 + "ms. Time: " +
                    ((endTime - startTime) - (1000000 * 50)) + "ms");
        }
    }

    @Override
    public final void stop() {
        LinkedList<Object> list = new LinkedList<>();
        try {
            stop(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            handleException(list, e);
        }

        postProcess(list, RobotStatus.MainStates.STOP);

        parent = null;
        robotContext.release();
        robotContext = null;
    }

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

    protected final int getLoopCount() {
        return loopCount;
    }

    protected final void skipNextLoop() {
        skipNextLoop++;
    }

    protected ExtensibleOpMode registerNewLoopOnEveryX(int loopX, OpModeLoop loop) {
        if (loops.containsKey(loopX)) {
            Log.w(TAG, "Loop already exists; replacing");
            loops.remove(loopX);
        }

        loops.put(loopX, loop);
        return this;
    }

    protected ExtensibleOpMode unregisterNewLoopOnEveryX(int loopX, OpModeLoop loop)
            throws IllegalStateException {
        if (loops.containsKey(loopX)) {
            loops.remove(loopX);
        } else {
            Log.e(TAG, "There is no registered loop replacement for " + loopX);
            throw new IllegalStateException("Loop has not registered replacement");
        }

        return this;
    }

    protected ExtensibleOpMode registerBeforeXLoop(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (beforeXLoop.containsKey(loop)) {
            beforeXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterEveryX(loopCountber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeX(int loopCount, int pos) {
        if (pos < 0 || pos > beforeXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastBeforeX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeX(int loopCount, String name) {
        return getCandidates(loopCount, name, beforeXLoop);
    }

    protected ExtensibleOpMode registerBeforeEveryX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (beforeEveryXLoop.containsKey(loop)) {
            beforeEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeEveryXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode requestChangeOfRegisterBeforeX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleOpMode unregisterBeforeEveryX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterEveryX(loopCountber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeEveryX(int loopCount, int pos) {
        if (pos < 0 || pos > beforeEveryXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        beforeEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastBeforeEveryX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeEveryX(int loopCount, String name) {
        return getCandidates(loopCount, name, beforeEveryXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterBeforeEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        beforeEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeEveryX() {
        return ImmutableMap.copyOf(beforeEveryXLoop);
    }

    protected ExtensibleOpMode registerAfterEveryX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (afterEveryXLoop.containsKey(loop)) {
            afterEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, afterEveryXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterEveryX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterEveryX(int loopCount, int pos) {
        if (pos < 0 || pos > afterEveryXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastEveryX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterEveryX(int loopCount, String name) {
        return getCandidates(loopCount, name, afterEveryXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterAfterEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterEveryX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleOpMode registerAfterX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (afterXLoop.containsKey(loop)) {
            afterXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, afterXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterX(int loopCount, int pos) {
        if (pos < 0 || pos > afterXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastAfterX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterX(int loopCount, String name) {
        return getCandidates(loopCount, name, afterXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterAfterX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterXLoop = new LinkedHashMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterX() {
        return ImmutableMap.copyOf(afterXLoop);
    }

    protected final RobotContext getContext() {
        return robotContext;
    }

    private boolean checkRunAssistantRemoval(
            int loopCountber, List<Integer> candidate, boolean isLast) throws IllegalStateException {
        if (candidate.size() < 1) {
            throw new IllegalStateException("Cannot remove something, if there is nothing");
        } else {
            if (candidate.size() != 1) {
                Log.w(TAG, "There are multiple removal candidates, removing the " +
                        (isLast ? "last" : "first") + ".");
                logRunAssistant(loopCountber, candidate);
            }
            return true;
        }
    }

    private boolean checkRunAssistantMap(Map<Integer, LinkedList<RunAssistant>> map) {
        for (Integer i : map.keySet()) {
            for (RunAssistant runAssistant : map.get(i)) {
                if (runAssistant == null) {
                    throw new NullPointerException();
                }
            }
        }
        return true;
    }

    private void createNewRunAssistantKey(
            int loop, RunAssistant assistant, Map<Integer, LinkedList<RunAssistant>> RunAssistantMap) {
        LinkedList<RunAssistant> assistants = new LinkedList<>();
        assistants.add(assistant);
        RunAssistantMap.put(loop, assistants);
    }

    private LinkedList<Integer> getCandidates(int loopCount, String name, Map<Integer, LinkedList<RunAssistant>> runAssistantMap) {
        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < runAssistantMap.get(loopCount).size(); i++) {
            RunAssistant secondItem = runAssistantMap.get(loopCount).get(i);
            if (secondItem.getClass().getSimpleName().indexOf(name) == 0) {
                list.add(i);
            }
        }
        return list;
    }

    private void logRunAssistant(int loopCountber, List<Integer> candidate) {
        for (int i : candidate) {
            Log.i(TAG, i + " " +
                    afterEveryXLoop.get(loopCountber).get(i).getClass().getSimpleName());
        }
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

    private void handleException(LinkedList<Object> list, Exception e) {
        Log.e(TAG,
                "An exception occurred running the OpMode " + getCallerClassName(e), e);

        robotContext.status().setCurrentStateType(RobotStatus.Type.IDK);
        RobotStatus.Type failure = robotContext.status().getCurrentStateType();
        if (onFailure(robotContext, failure, RobotStatus.MainStates.EXCEPTION, e) >= 0) {
            robotContext.status().setCurrentStateType(RobotStatus.Type.SUCCESS);
            for (Object o : list) {
                onFailure(robotContext, failure, RobotStatus.MainStates.EXCEPTION, o);
            }
        } else {
            robotContext.status().setCurrentStateType(RobotStatus.Type.FAILURE);
            RobotLog.setGlobalErrorMsg(e.toString());
            Throwables.propagate(e);
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

    protected ExtensibleGamepad gamepad1() {
        return robotContext.gamepad1();
    }

    protected ExtensibleGamepad gamepad2() {
        return robotContext.gamepad2();
    }

    protected ExtensibleHardwareMap hardwareMap() {
        return robotContext.hardwareMap();
    }

    protected ExtensibleTelemetry telemetry() {
        return robotContext.telemetry();
    }
}
