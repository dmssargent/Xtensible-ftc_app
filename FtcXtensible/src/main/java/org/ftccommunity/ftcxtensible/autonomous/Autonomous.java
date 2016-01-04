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
package org.ftccommunity.ftcxtensible.autonomous;

import com.google.common.base.Throwables;

import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


public class Autonomous implements Runnable {
    private static final String TAG = "AUTONOMOUS_RUNNER::";
    private final RobotContext robotContext;
    private RunLevel[] levels;
    // private RunLevel init;
    private int initLevel;
    // private RunLevel finish;
    private int stopLevel;
    // private RunLevel safe;
    private int safeLevel;
    private boolean done;

    public Autonomous(RobotContext state) {
        robotContext = state;
        done = false;
    }

    public Autonomous(RunLevel[] states, RobotContext state) {
        this(state);
        checkNotNull(states);

        levels = states;

        setInitState(0);
        setSafeState(states.length - 1);
        setFinishState(states.length - 1);
    }


    @NotNull
    public synchronized RunLevel[] getRunLevels() {
        return levels;
    }

    public synchronized void setRunLevels(@NotNull RunLevel[] newLevels) {
        checkState(stopLevel == initLevel, "Stop and Init Levels are the same, please specify the " +
                "new levels.");

        for (RunLevel level : newLevels) {
            checkNotNull(level);
        }

        //Set the new important levels
        setInitState(initLevel);
        setFinishState(stopLevel);
        setSafeState(safeLevel);
    }

    public synchronized void setRunLevels(@NotNull RunLevel[] newLevels,
                                          int newInit, int newFinish, int newSafe) {
        checkArgument(newInit < newLevels.length && newInit >= 0,
                "init isn't a invalid runlevel");
        checkArgument(newFinish < newLevels.length && newFinish >= 0,
                "finish isn't a invalid runlevel");
        checkArgument(newSafe < newLevels.length && newSafe >= 0 && newInit >= 0,
                "safe isn't a invalid runlevel");

        initLevel = newInit;
        stopLevel = newFinish;
        safeLevel = newSafe;

        //Pass to main setRunLevels
        setRunLevels(newLevels);
    }

    public void runInit() {
        runLevel(initLevel);
    }

    public void runFinishLevel() {
        runLevel(stopLevel);
    }

    public synchronized void setInitState(int level) {
        checkState(levels != null);
        isValidLevelNum(level);

        initLevel = level;
    }

    public synchronized void setFinishState(int level) {
        checkState(levels != null);
        checkElementIndex(level, levels.length);

        stopLevel = level;
    }

    public synchronized void setSafeState(int level) {
        isValidLevelNum(level);
        safeLevel = level;
    }

    public int runLevel(int level) {
        isValidLevelNum(level);

        int execReturn = -1;
        try {
            execReturn = levels[level].execute();
        } catch (Exception ex) {
            if (!levels[level].safe()) {
                Throwables.propagate(ex);
            }
        }

        //Log result
        robotContext.log().i(TAG, "runLevel " + level + " returned a status code of " + execReturn);

        //Check if zero has been returned, if so, continue
        if (execReturn == 0) {
            RobotLog.i("runLevel " + level + " succeeded.");
            return 0;
        } else { // Zero was not returned
            if (execReturn > 0) {
                robotContext.log().w(TAG, "runLevel " + level + " returned a status code of " + execReturn);
                levels[level].safe(execReturn);
            } else {
                robotContext.log().i(TAG, "runLevel " + level + " returned a status code of " + execReturn);
                checkState(levels[level].safe());
            }
        }

        return execReturn;
    }


    public void start() {
        runLevel(initLevel);
    }

    public void run() {
        //Run everything between initLevel and stopLevel
        for (int i = initLevel + 1; i < stopLevel; i++) {
            try {
                runLevel(i);
            } catch (RuntimeException ex) {
                RobotLog.logStacktrace(ex);
                Throwables.propagate(ex);
                break;
            }
        }

        done = true;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public void close() {
        runLevel(stopLevel);
    }

    public boolean isValidState() {
        return levels != null;
    }

    public void isValidLevelNum(int level) throws
            IllegalArgumentException, IndexOutOfBoundsException {
        checkState(isValidState());
        checkElementIndex(level, levels.length);
    }
}
