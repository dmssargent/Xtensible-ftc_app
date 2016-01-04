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

package org.ftc.opmodes.statemachine;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import org.ftccommunity.ftcxtensible.internal.Alpha;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Alpha
public class BetterFiniteStateMachine<SYMBOL extends BetterFiniteStateMachine.StateNode, T extends OpMode> {
    private SYMBOL index;
    private LinkedHashMap<SYMBOL, State> states;
    private T opMode;

    private BetterFiniteStateMachine(T opMode) {
        this.opMode = checkNotNull(opMode);
        states = new LinkedHashMap<SYMBOL, State>();

    }

    private BetterFiniteStateMachine(T opMode, SYMBOL... symbols) {
        this(opMode);
        for (SYMBOL entry :
                symbols) {
            states.put(entry, State.INIT);
        }
        index = symbols[0];
    }

    private static <T> T checkNotNull(T ref) {
        if (ref == null) {
            throw new NullPointerException();
        }

        return ref;
    }

    public void execute() {

        State state = states.get(index);
        //if (index.name().equalsIgnoreCase("NOTHING"))

        // Inject requested variables
        inject(index, "hardwareMap", HardwareMap.class, opMode.hardwareMap);
        inject(index, "gamepad1", Gamepad.class, opMode.gamepad1);
        inject(index, "gamepad2", Gamepad.class, opMode.gamepad2);
        inject(index, "telemetry", Telemetry.class, opMode.telemetry);
        inject(index, "opMode", opMode.getClass(), opMode);

        try {
            index.execute();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (index.stateChange()) {
//            index = ++index;
//            if (index > states.size()) {
//                index = -1;
//            }
        }
    }

    public SYMBOL index() {
        return index;
    }

    public State state() {
        return states.get(index);
    }

    public void changeState(SYMBOL SYMBOL) {
        index = checkNotNull(SYMBOL);
    }

    private <TYPE> void inject(SYMBOL object, String name, Class<? extends TYPE> type, TYPE value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            if (!field.getType().equals(type)) {
                return;
            }

            boolean wasAccessible = field.isAccessible();
            if (!wasAccessible) {
                field.setAccessible(true);
            }

            if (field.get(object) == null) {
                field.set(object, value);
            }

            if (!wasAccessible) {
                field.setAccessible(false);
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
    }

    private LinkedList<SYMBOL> getListFromEnum(SYMBOL[] SYMBOLs) {
        return new LinkedList<SYMBOL>(Arrays.asList(SYMBOLs));
    }

    public enum State {
        INIT, LOADED, EXECUTE, FINISHED, ABORT
    }

    public interface StateNode<T> {
        boolean stateChange();

        void execute() throws Exception;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface IsStateNode {
    }

    public static class Builder<SYMBOL extends StateNode, OP_MODE extends OpMode> {
        private LinkedList<SYMBOL> states;
        private OP_MODE opMode;

        public Builder() {
            states = new LinkedList<SYMBOL>();
        }

        public Builder<SYMBOL, OP_MODE> setActiveOpMode(OP_MODE opMode) {
            this.opMode = checkNotNull(opMode);

            return this;
        }

        @SafeVarargs
        public final Builder<SYMBOL, OP_MODE> registerStates(SYMBOL... symbols) {
            Collections.addAll(states, checkNotNull(symbols));

            return this;
        }

        public BetterFiniteStateMachine<SYMBOL, OP_MODE> build() {
            if (opMode == null) {
                throw new IllegalArgumentException();
            }

            if (states.size() == 0) {
                for (Class<?> state :
                        opMode.getClass().getDeclaredClasses()) {
                    if (state.isAnnotationPresent(IsStateNode.class)) {
                        try {
                            if (state.isAssignableFrom(StateNode.class)) {
                                state.getConstructor().setAccessible(true);
                                //suppress unchecked
                                states.add((SYMBOL) state.newInstance());
                            }
                        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException ex) {
                            Log.e(BetterFiniteStateMachine.class.getSimpleName(), "Failed to load " + state.getSimpleName(), ex);
                        }
                    }
                }
            }

            return new BetterFiniteStateMachine<SYMBOL, OP_MODE>(opMode, (SYMBOL[]) states.toArray());
        }
    }
}

