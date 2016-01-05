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

import com.google.common.collect.ImmutableMap;

import android.util.Log;

import org.ftccommunity.ftcxtensible.interfaces.OpModeLoop;
import org.ftccommunity.ftcxtensible.interfaces.RunAssistant;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ExtensibleLoopManager {
    private static final String TAG = "";
    private final LinkedHashMap<Integer, LinkedList<RunAssistant>> beforeXLoop;
    private final TreeMap<Integer, OpModeLoop> loops;
    private final ExtensibleOpMode robotOp;
    private TreeMap<Integer, LinkedList<RunAssistant>> beforeEveryXLoop;
    private TreeMap<Integer, LinkedList<RunAssistant>> afterEveryXLoop;
    private LinkedHashMap<Integer, LinkedList<RunAssistant>> afterXLoop;

    public ExtensibleLoopManager(ExtensibleOpMode extensibleOpMode) {
        robotOp = extensibleOpMode;
        loops = new TreeMap<>();
        beforeXLoop = new LinkedHashMap<>();
        beforeEveryXLoop = new TreeMap<>();
        afterEveryXLoop = new TreeMap<>();
        afterXLoop = new LinkedHashMap<>();
    }

    protected ExtensibleLoopManager registerNewLoopOnEveryX(int loopX, OpModeLoop loop) {
        if (loops.containsKey(loopX)) {
            Log.w(TAG, "Loop already exists; replacing");
            loops.remove(loopX);
        }

        loops.put(loopX, loop);
        return this;
    }

    protected ExtensibleLoopManager unregisterNewLoopOnEveryX(int loopX, OpModeLoop loop)
            throws IllegalStateException {
        checkState(loops.containsKey(loopX), "Loop has not registered replacement for " + loopX);
        loops.remove(loopX);

        return this;
    }

    protected ExtensibleLoopManager registerBeforeXLoop(int loop, RunAssistant assistant) {
        checkNotNull(assistant);

        if (beforeXLoop.containsKey(loop)) {
            beforeXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeXLoop);
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterBeforeX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterEveryX(loopCountber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterBeforeX(int loopCount, int pos) {
        checkElementIndex(pos, beforeXLoop.get(loopCount).size());

        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleLoopManager unregisterLastBeforeX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeX(int loopCount, String name) {
        return getCandidates(loopCount, name, beforeXLoop);
    }

    protected ExtensibleLoopManager registerBeforeEveryX(int loop, RunAssistant assistant) {
        checkNotNull(assistant);

        if (beforeEveryXLoop.containsKey(loop)) {
            beforeEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeEveryXLoop);
        }

        return this;
    }

    protected ExtensibleLoopManager requestChangeOfRegisterBeforeX(Map<Integer, LinkedList<RunAssistant>> map) {
        checkNotNull(map);

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleLoopManager unregisterBeforeEveryX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterEveryX(loopCountber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterBeforeEveryX(int loopCount, int pos) {
        checkElementIndex(pos, beforeEveryXLoop.get(loopCount).size());

        beforeEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleLoopManager unregisterLastBeforeEveryX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeEveryX(int loopCount, String name) {
        return getCandidates(loopCount, name, beforeEveryXLoop);
    }

    protected ExtensibleLoopManager requestChangeOfRegisterBeforeEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        checkNotNull(map);

        checkRunAssistantMap(map);
        beforeEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeEveryX() {
        return ImmutableMap.copyOf(beforeEveryXLoop);
    }

    protected ExtensibleLoopManager registerAfterEveryX(int loop, RunAssistant assistant) {
        checkNotNull(assistant);

        if (afterEveryXLoop.containsKey(loop)) {
            afterEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, afterEveryXLoop);
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterAfterEveryX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterAfterEveryX(int loopCount, int pos) {
        checkElementIndex(pos, afterEveryXLoop.size());

        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleLoopManager unregisterLastEveryX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterEveryX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterEveryX(int loopCount, String name) {
        return getCandidates(loopCount, name, afterEveryXLoop);
    }

    protected ExtensibleLoopManager requestChangeOfRegisterAfterEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        checkNotNull(map);

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterEveryX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleLoopManager registerAfterX(int loop, RunAssistant assistant) {
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

    protected ExtensibleLoopManager unregisterAfterX(int loopCountber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, false)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleLoopManager unregisterAfterX(int loopCount, int pos) {
        checkElementIndex(pos, afterXLoop.size());

        afterXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleLoopManager unregisterLastAfterX(int loopCountber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopCountber, name);
        if (checkRunAssistantRemoval(loopCountber, candidate, true)) {
            unregisterAfterX(loopCountber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterX(int loopCount, String name) {
        return getCandidates(loopCount, name, afterXLoop);
    }

    protected ExtensibleLoopManager requestChangeOfRegisterAfterX(Map<Integer, LinkedList<RunAssistant>> map) {
        checkNotNull(map);
        checkRunAssistantMap(map);
        afterXLoop = new LinkedHashMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterX() {
        return ImmutableMap.copyOf(afterXLoop);
    }


    private boolean checkRunAssistantRemoval(
            int loopCounter, List<Integer> candidates, boolean isLast) throws IllegalStateException {
        checkArgument(candidates.size() > 1, "Cannot remove something, if there is nothing");

        if (candidates.size() != 1) {
            Log.w(TAG, "There are multiple removal candidates, removing the " +
                    (isLast ? "last" : "first") + ".");
            logRunAssistant(loopCounter, candidates);
        }
        return true;
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

    public LinkedList<RunAssistant> getPreloopAssistants(int loopCount) {
        LinkedList<RunAssistant> assistants = new LinkedList<>();

        if (beforeXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : beforeXLoop.get(loopCount)) {
                assistants.add(assistant);
            }
        }

        if (beforeEveryXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : beforeEveryXLoop.get(loopCount)) {
                assistants.add(assistant);
            }
        }

        return assistants;
    }

    public LinkedList<RunAssistant> getPostLoopAssistants(int loopCount) {
        LinkedList<RunAssistant> assistants = new LinkedList<>();
        if (afterEveryXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : afterEveryXLoop.get(loopCount)) {
                assistants.add(assistant);
            }
        }

        if (afterXLoop.containsKey(loopCount)) {
            for (RunAssistant assistant : afterXLoop.get(loopCount)) {
                assistants.add(assistant);
            }
        }

        return assistants;
    }
}




