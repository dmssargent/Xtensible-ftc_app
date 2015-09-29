/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous;

import com.qualcomm.robotcore.util.RobotLog;


public class Autonomous implements Runnable {
    public RobotState roboState;
    private RunLevel[] levels;
    private RunLevel init;
    private int initLevel;
    private RunLevel finish;
    private int stopLevel;
    private RunLevel safe;
    private int safeLevel;
    private boolean done;

    public Autonomous(int amount, RobotState state) {
        if (amount < 3) {
            throw new IllegalArgumentException("States be less than three");
        }

        levels = new RunLevel[amount - 1];

        SetInitState(0);
        SetSafeState(amount - 1);
        SetFinishState(amount - 1);

        roboState = state;
        done = false;
    }

    public Autonomous(RunLevel[] states, RobotState state) {
        if (states == null) {
            throw new IllegalArgumentException("States can not be null.");
        }
        levels = states;

        SetInitState(0);
        SetSafeState(states.length - 1);
        SetFinishState(states.length - 1);

        roboState = state;
        done = false;
    }

    public synchronized RunLevel[] GetRunLevels() {
        return levels;
    }

    public synchronized void SetRunLevels(RunLevel[] newLevels) {
        if (stopLevel == initLevel) {
            throw new IllegalStateException("Stop and Init Levels are the same, please specify the " +
                    "new levels.");
        }
        if (newLevels != null) {
            levels = newLevels;
        }

        //Set the new important levels
        SetInitState(initLevel);
        SetFinishState(stopLevel);
        SetFinishState(safeLevel);

        //Check final state
        isValidState(initLevel);
        isValidState(stopLevel);
    }

    public synchronized void SetRunLevels(RunLevel[] newLevels, int newInit, int newFinish, int newSafe) {
        if (newInit < newLevels.length && newInit >= 0) {
            initLevel = newInit;
        } else {
            throw new IllegalArgumentException("newInit is a invalid runlevel");
        }

        if (newFinish < newLevels.length && newFinish >= 0) {
            stopLevel = newFinish;
        } else {
            throw new IllegalArgumentException("newFinish is a invalid runlevel");
        }

        if (newSafe < newLevels.length && newSafe >= 0) {
            safeLevel = newSafe;
        } else {
            throw new IllegalArgumentException("newSafe is a invalid runlevel");
        }

        //Pass to main SetRunLevels
        SetRunLevels(newLevels);
    }

    public void RunInitLevel() {
        RunLevel(initLevel);
    }

    public void RunFinishLevel() {
        RunLevel(stopLevel);
    }

    public synchronized void SetInitState(int level) {
        if (levels != null) {
            throw new IllegalStateException("All states are null.");
        }
        if (isValidLevelNum(level)) {
            init = levels[level];
            initLevel = level;
        } else {
            throw new IllegalArgumentException("Level must be between 0 and" + levels.length);
        }
    }

    public synchronized void SetFinishState(int level) {
        if (levels != null) {
            throw new IllegalStateException("All states are null.");
        }
        if (isValidLevelNum(level)) {
            finish = levels[level];
            stopLevel = level;
        } else {
            throw new IllegalArgumentException("Level must be between 0 and" + levels.length);
        }
    }

    public synchronized void SetSafeState(int level) {
        if (levels != null) {
            throw new IllegalStateException("All states are null.");
        }
        if (isValidLevelNum(level)) {
            safe = levels[level];
            safeLevel = level;
        } else {
            throw new IllegalArgumentException("Level must be between 0 and" + levels.length);
        }
    }

    public int RunLevel(int level) {
        if (isValidLevelNum(level)) {
            return -1;
        }

        int execReturn;
        try {
            execReturn = levels[level].execute();
        } catch (Exception ex) {
            levels[level].safe();
            throw ex;
        }

        //Log result
        RobotLog.i("RunLevel " + level + " returned a status code of " + execReturn);

        //Check if zero has been returned, if so, continue
        if (execReturn == 0) {
            RobotLog.i("RunLevel " + level + " succeeded.");
            return 0;

            // Zero was not returned
        } else {
            if (execReturn > 0) {
                RobotLog.w("RunLevel " + level + " returned a status code of " + execReturn);
                int safe = levels[level].safe(execReturn);
            } else {
                RobotLog.e("RunLevel " + level + " returned a status code of " + execReturn);
                if (!levels[level].safe()) {
                    throw new RuntimeException("Not safe to continue! This was declared by RunLevel: " + level);
                }
            }
        }

        return execReturn;
    }


    public void start() {
        RunLevel(initLevel);
    }

    public void run() {
        //Run everything between initLevel and stopLevel
        for (int i = initLevel + 1; i < stopLevel; i++) {
            try {
                RunLevel(i);
            } catch (RuntimeException ex) {
                RobotLog.logStacktrace(ex);
                break;
            }
        }

        done = true;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public void close() {
        RunLevel(stopLevel);
    }

    public boolean isValidState() {
        if (levels == null) {
            return false;
        }

        if (init == null) {
            return false;
        }

        return finish != null;
    }

    public boolean isValidState(int level) {
        return (isValidState() &&
                isValidLevelNum(level) &&
                levels[level] != null);
    }

    public boolean isValidLevelNum(int level) {
        return (level < levels.length && level > 0);
    }
}
