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
package org.firstinspires.ftc.teamcode.statemachine;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.internal.Alpha;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;

@Alpha
public class AdvancedFiniteStateMachine<STATE extends Enum & AdvancedFiniteStateMachine.State, T extends OpMode> {
    public static final int REMAIN = 0;
    public static final int NEXT = -2;
    public static final int WAIT = -3;
    private final T opMode;
    private int index;
    private LinkedList<STATE> states;

    private AdvancedFiniteStateMachine(T opMode) {
        this.opMode = checkNotNull(opMode);
    }

    /**
     * Creates an Finite State Machine with the states described in the class declaration of the
     * <code>enum</code>. The OpMode is a reference to your OpMode to get the dependencies of state,
     * and inject them into the state
     *
     * @param enumeration the class of the enum containing the states
     * @param opMode      the active {@link OpMode} to get dependencies from
     */
    public AdvancedFiniteStateMachine(Class<? extends STATE> enumeration, T opMode) throws NullPointerException {
        this(opMode);
        states = new LinkedList<>(EnumSet.allOf(checkNotNull(enumeration)));
        index = 0;
    }

    /**
     * Creates a Finite State Machine with the given {@link State} array being the available states.
     * The OpMode is a reference to your OpMode to get the dependencies of state, and inject them
     * into the state
     *
     * @param states an array of states
     * @param opMode the active {@link OpMode} to get dependencies from
     * @throws NullPointerException when either the <code>states</code> or <code>opMode</code>
     *                              parameter is null
     */
    public AdvancedFiniteStateMachine(STATE[] states, T opMode) throws NullPointerException {
        this(checkNotNull(opMode));
        this.states = getListFromEnum(checkNotNull(states));
        index = 0;
    }

    /**
     * Creates a Finite State Machine with the given start {@link State}. The rest of the states are
     * then infered to be the other elements within the enum that the start state is. The OpMode is
     * a reference to your OpMode to get the dependencies of state, and inject them into the state
     *
     * @param state  the start state of the Finite State Machine
     * @param opMode the active {@link OpMode} to get dependencies from
     * @throws NullPointerException when either the <code>states</code> or <code>opMode</code>
     *                              parameter is null
     */
    public AdvancedFiniteStateMachine(STATE state, T opMode) throws NullPointerException {
        this(checkNotNull(state.getDeclaringClass()), opMode);
        index = state.ordinal();
    }

    private static <T> T checkNotNull(T ref) {
        if (ref == null) {
            throw new NullPointerException();
        }

        return ref;
    }

    /**
     * Executes the current state once by running the {@link State#execute()}, then checks to see if
     * the state needs to be changed by running the {@link State#changeState(Enum)} method of the
     * currently running state
     */
    public void execute() {
        if (index == -1) {
            return;
        }

        STATE state = states.get(index);
        if (state.name().equalsIgnoreCase("NOTHING"))

            // Inject requested variables
            inject(state, "hardwareMap", HardwareMap.class, opMode.hardwareMap);
        inject(state, "gamepad1", Gamepad.class, opMode.gamepad1);
        inject(state, "gamepad2", Gamepad.class, opMode.gamepad2);
        inject(state, "telemetry", Telemetry.class, opMode.telemetry);
        inject(state, "opMode", opMode.getClass(), opMode);

        state.execute();
    }

    /**
     * The index of the current {@link State} in the set of possible <code>State</code>s
     *
     * @return the index of the current states
     */
    public int index() {
        return index;
    }

    /**
     * Returns the currently running state
     *
     * @return the current state
     */
    public STATE state() {
        return states.get(index);
    }

    /**
     * Changes the state to specified {@link State}
     *
     * @param state the new state to switch to
     * @throws NullPointerException if the given state is <code>null</code>
     */
    public void changeState(STATE state) throws NullPointerException {
        index = checkNotNull(state).ordinal();
    }

    private <TYPE> void inject(STATE object, String name, Class<? extends TYPE> type, TYPE value) {
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

    private LinkedList<STATE> getListFromEnum(STATE[] states) {
        return new LinkedList<>(Arrays.asList(states));
    }

    /**
     * The definition of a state for use by this FSM.
     */
    public interface State {
        /**
         * Returns whether or not to change the state to the next state, called once for every
         * {@link AdvancedFiniteStateMachine#execute()} that is called, and after the {@link
         * #execute()} is called
         *
         * @return <code>true</code>, if the state should transition to the next state;
         * <code>false</code> otherwise
         */
        int stateChange();

        /**
         * The main executing method, called once for each time {@link AdvancedFiniteStateMachine#execute()}
         * is called, and before {@link #stateChange()} is called.
         */
        void execute();
    }

    public interface StateEnum {
        State waitState();
    }
}

