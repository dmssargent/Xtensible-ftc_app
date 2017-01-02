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

import android.util.Log;

import com.google.common.collect.Range;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.ftccommunity.ftcxtensible.core.exceptions.RuntimeIOException;
import org.ftccommunity.ftcxtensible.core.internal.HashUtil;
import org.ftccommunity.ftcxtensible.gamepad.Axis;
import org.ftccommunity.ftcxtensible.gamepad.GamepadButton;
import org.ftccommunity.ftcxtensible.gamepad.GamepadEvent;
import org.ftccommunity.ftcxtensible.gamepad.GamepadTrigger;
import org.ftccommunity.ftcxtensible.gamepad.learning.GamepadRecord;
import org.ftccommunity.ftcxtensible.gamepad.learning.GamepadState;
import org.ftccommunity.ftcxtensible.interfaces.GamepadAxis;
import org.ftccommunity.ftcxtensible.interfaces.JoystickScaler;
import org.ftccommunity.ftcxtensible.interfaces.RobotAction;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
import org.ftccommunity.ftcxtensible.math.PolarCoordinates;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The extensible Gamepad, based on <code>{@link Gamepad}</code>. This takes care of multiple issues
 * of the design of the original <code>Gamepad</code>, including code readablity, design, and
 * extending the capabilities of what the Gamepad can do.
 *
 * @author David Sargent
 * @since 0.1
 */
@Alpha
public class ExtensibleGamepad implements Closeable {
    public final Joystick leftJoystick;
    public final Joystick rightJoystick;
    public final Dpad dpad;
    private boolean a;
    private boolean b;
    private boolean x;
    private boolean y;
    private boolean guide;
    private boolean start;
    private boolean back;
    private boolean leftBumper;
    private boolean rightBumper;
    private float leftTrigger;
    private float rightTrigger;
    private long timestamp;
    private int userDefinedRight;
    private int userDefinedLeft;
    private JoystickScaler leftScaler;
    private JoystickScaler rightScaler;

    private transient boolean recording;
    private transient boolean playingBack;
    private transient GamepadRecord gamepadRecord;

    private boolean hasBeenClosed = false;
    private List<GamepadEvent> handlers;

    /**
     * Setup a basic gamepad; be sure to call {@link ExtensibleGamepad#updateGamepad} to make this
     * safe to use
     */
    public ExtensibleGamepad() {
        leftJoystick = new Joystick();
        rightJoystick = new Joystick();
        dpad = new Dpad();

        leftScaler = new PlainJoystickScaler();
        rightScaler = new PlainJoystickScaler();

        recording = false;
        playingBack = false;

        handlers = new LinkedList<>();
    }

    public ExtensibleGamepad(final RobotContext ctx, Gamepad gp) {
        this();
        updateGamepad(ctx, gp);
    }

    public ExtensibleGamepad(Gamepad gp) {
        this();
        updateGamepad(gp);
    }

    /**
     * Add scaler algorithms to modify the joystick input
     *
     * @param left  a <code>{@link JoystickScaler}</code> with a custom scaler to use on the left
     *              Joystick
     * @param right a <code>JoystickScaler</code> with a custom scaler to use on the right Joystick
     * @return this object to continue work on
     */
    public ExtensibleGamepad setupJoystickScalers(JoystickScaler left, JoystickScaler right) {
        if (left == null || right == null) {
            throw new NullPointerException();
        }
        leftScaler = left;
        rightScaler = right;
        return this;
    }

    /**
     * Updates this to the status of the provided Gamepad (recast this from a <code>{@link
     * Gamepad}</code>)
     *
     * @param ctx the Robot Context (nullable, depending on the given <code>{@link
     *            JoystickScaler}</code>)
     * @param gp  the <code>Gamepad</code> to cast into
     */
    public synchronized void updateGamepad(final RobotContext ctx, Gamepad gp) {
        if (playingBack) {
            GamepadState state = gamepadRecord.nextRecord();
            if (state == null) {
                Log.w("GAMEPAD", "Stopping playback due to end-of-record");
                stopPlayback();
                updateGamepad(ctx, gp);
                return;
            }
            a = state.a;
            b = state.b;
            x = state.x;
            y = state.y;

            guide = state.guide;
            start = state.start;
            back = state.back;

            leftBumper = state.leftBumper;
            rightBumper = state.rightBumper;

            leftTrigger = state.leftTrigger;
            rightTrigger = state.rightTrigger;

            dpad.update(state.dpad.isUpPressed(), state.dpad.isDownPressed(),
                    state.dpad.isRightPressed(), state.dpad.isLeftPressed());

            rightJoystick().update(state.rightJoystick.x, state.rightJoystick.y, state.rightJoystick.pressed);

            userDefinedLeft = getLeftScaler().userDefinedLeft(ctx, this);
            userDefinedRight = getRightScaler().userDefinedRight(ctx, this);

            double leftX = state.leftJoystick.x;
            double leftY = state.leftJoystick.y;
            leftX = getLeftScaler().scaleX(this, leftX);
            leftY = getLeftScaler().scaleY(this, leftY);
            leftJoystick().update(leftX, leftY, state.leftJoystick.pressed);

            double rightX = state.rightJoystick.x;
            double rightY = state.rightJoystick.y;
            rightX = getRightScaler().scaleX(this, rightX);
            rightY = getRightScaler().scaleY(this, rightY);
            rightJoystick().update(rightX, rightY, state.rightJoystick.pressed);

            timestamp = System.nanoTime();
        } else {
            a = gp.a;
            b = gp.b;
            x = gp.x;
            y = gp.y;

            guide = gp.guide;
            start = gp.start;
            back = gp.back;

            leftBumper = gp.left_bumper;
            rightBumper = gp.right_bumper;

            leftTrigger = gp.left_trigger;
            rightTrigger = gp.right_trigger;

            timestamp = gp.timestamp;

            dpad.update(gp.dpad_up, gp.dpad_down, gp.dpad_right, gp.dpad_left);

            rightJoystick().update(gp.right_stick_x, gp.right_stick_y, gp.right_stick_button);

            userDefinedLeft = getLeftScaler().userDefinedLeft(ctx, this);
            userDefinedRight = getRightScaler().userDefinedRight(ctx, this);

            double leftX = gp.left_stick_x;
            double leftY = gp.left_stick_y;
            leftX = getLeftScaler().scaleX(this, leftX);
            leftY = getLeftScaler().scaleY(this, leftY);
            leftJoystick().update(leftX, leftY, gp.left_stick_button);

            double rightX = gp.right_stick_x;
            double rightY = gp.right_stick_y;
            rightX = getRightScaler().scaleX(this, rightX);
            rightY = getRightScaler().scaleY(this, rightY);
            rightJoystick().update(rightX, rightY, gp.right_stick_button);
        }

        if (recording) {
            gamepadRecord.addRecord(new GamepadState(System.nanoTime(), leftJoystick, rightJoystick, dpad,
                    a, b, x, y,
                    guide, start, back,
                    leftBumper, rightBumper, leftTrigger, rightTrigger));
        }

        for (GamepadEvent event : handlers) {
            if (checkForExecuteGamepadCallback(event.object, event.valueToExecuteOn, event.other)) {
                event.action.perform();
            }
        }
    }

    /**
     * Updates this to the status of the provided Gamepad (recast this from a <code>{@link
     * Gamepad}</code>)
     *
     * @param gp the <code>Gamepad</code> to cast into
     */
    public synchronized void updateGamepad(Gamepad gp) {
        if (playingBack) {
            GamepadState state = gamepadRecord.nextRecord();
            if (state == null) {
                Log.w("GAMEPAD", "Stopping playback due to end-of-record");
                stopPlayback();
                updateGamepad(gp);
                return;
            }
            a = state.a;
            b = state.b;
            x = state.x;
            y = state.y;

            guide = state.guide;
            start = state.start;
            back = state.back;

            leftBumper = state.leftBumper;
            rightBumper = state.rightBumper;

            leftTrigger = state.leftTrigger;
            rightTrigger = state.rightTrigger;

            dpad.update(state.dpad.isUpPressed(), state.dpad.isDownPressed(),
                    state.dpad.isRightPressed(), state.dpad.isLeftPressed());

            rightJoystick().update(state.rightJoystick.x, state.rightJoystick.y, state.rightJoystick.pressed);

            //userDefinedLeft = getLeftScaler().userDefinedLeft(ctx, this);
            //userDefinedRight = getRightScaler().userDefinedRight(ctx, this);

            double leftX = state.leftJoystick.x;
            double leftY = state.leftJoystick.y;
            leftX = getLeftScaler().scaleX(this, leftX);
            leftY = getLeftScaler().scaleY(this, leftY);
            leftJoystick().update(leftX, leftY, state.leftJoystick.pressed);

            double rightX = state.rightJoystick.x;
            double rightY = state.rightJoystick.y;
            rightX = getRightScaler().scaleX(this, rightX);
            rightY = getRightScaler().scaleY(this, rightY);
            rightJoystick().update(rightX, rightY, state.rightJoystick.pressed);

            timestamp = System.nanoTime();
        } else {
            a = gp.a;
            b = gp.b;
            x = gp.x;
            y = gp.y;

            guide = gp.guide;
            start = gp.start;
            back = gp.back;

            leftBumper = gp.left_bumper;
            rightBumper = gp.right_bumper;

            leftTrigger = gp.left_trigger;
            rightTrigger = gp.right_trigger;

            timestamp = gp.timestamp;

            dpad.update(gp.dpad_up, gp.dpad_down, gp.dpad_right, gp.dpad_left);

            rightJoystick().update(gp.right_stick_x, gp.right_stick_y, gp.right_stick_button);

            //userDefinedLeft = getLeftScaler().userDefinedLeft(ctx, this);
            //userDefinedRight = getRightScaler().userDefinedRight(ctx, this);

            double leftX = gp.left_stick_x;
            double leftY = gp.left_stick_y;
            leftX = getLeftScaler().scaleX(this, leftX);
            leftY = getLeftScaler().scaleY(this, leftY);
            leftJoystick().update(leftX, leftY, gp.left_stick_button);

            double rightX = gp.right_stick_x;
            double rightY = gp.right_stick_y;
            rightX = getRightScaler().scaleX(this, rightX);
            rightY = getRightScaler().scaleY(this, rightY);
            rightJoystick().update(rightX, rightY, gp.right_stick_button);
        }

        if (recording) {
            gamepadRecord.addRecord(new GamepadState(System.nanoTime(), leftJoystick, rightJoystick, dpad,
                    a, b, x, y,
                    guide, start, back,
                    leftBumper, rightBumper, leftTrigger, rightTrigger));
        }

        for (GamepadEvent event : handlers) {
            if (checkForExecuteGamepadCallback(event.object, event.valueToExecuteOn, event.other)) {
                event.action.perform();
            }
        }
    }


    /**
     * Gets the status of Button A
     *
     * @return is button A pressed
     */
    public boolean isAPressed() {
        return a;
    }

    /**
     * Gets the status of Button B
     *
     * @return is button B pressed
     */
    public boolean isBPressed() {
        return b;
    }

    /**
     * Gets the status of Button X
     *
     * @return is button X pressed
     */
    public boolean isXPressed() {
        return x;
    }

    /**
     * Gets the status of Button Y
     *
     * @return is button Y pressed
     */
    public boolean isYPressed() {
        return y;
    }

    /**
     * Gets the status of the guide button
     *
     * @return is the Guide button pressed
     */
    public boolean isGuidePressed() {
        return guide;
    }

    /**
     * Gets the status of the Start button
     *
     * @return is the Start button pressed
     */
    public boolean isStartPressed() {
        return start;
    }

    /**
     * Gets the status of the back button
     *
     * @return is the Back button pressed
     */
    public boolean isBackPressed() {
        return back;
    }

    /**
     * Gets the status of the Left Bumper (on the Gamepad)
     *
     * @return is the Left Bumper pressed
     */
    public boolean isLeftBumperPressed() {
        return leftBumper;
    }

    /**
     * Gets the status of the Right Bumper (on the Gamepad)
     *
     * @return is the Right Bumper pressed
     */
    public boolean isRightBumperPressed() {
        return rightBumper;
    }

    /**
     * Gets the value of the Left Trigger (no idea what this is; we are just exposing it)
     *
     * @return the value of the Left Trigger
     */
    public float getLeftTrigger() {
        return leftTrigger;
    }

    /**
     * Gets the value of the Right Trigger (again no idea what the value means; we are just exposing
     * it)
     *
     * @return the value of the Right Trigger
     */
    public float getRightTrigger() {
        return rightTrigger;
    }

    /**
     * The time the joystick was update (in milliseconds) from the FTC SDK
     *
     * @return the time in milliseconds of last update
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the custom value of <code>{@link JoystickScaler#userDefinedRight(RobotContext,
     * ExtensibleGamepad)}</code>
     *
     * @return the value that the code returned
     */
    public int getUserDefinedRight() {
        return userDefinedRight;
    }

    /**
     * Gets the custom value of <code>{@link JoystickScaler#userDefinedLeft(RobotContext,
     * ExtensibleGamepad)}</code>
     *
     * @return the value that the code returned
     */
    public int getUserDefinedLeft() {
        return userDefinedLeft;
    }

    /**
     * Gets the left joystick
     *
     * @return the left <code> {@link ExtensibleGamepad.Joystick} </code> of this controller
     */
    public Joystick leftJoystick() {
        return leftJoystick;
    }

    /**
     * Gets the right joystick
     *
     * @return the right <code>Joystick</code> of this controller
     */
    public Joystick rightJoystick() {
        return rightJoystick;
    }

    /**
     * Gets the left joystick scaler
     *
     * @return <code>{@link JoystickScaler}</code> of the left <code>Joystick</code>
     */
    public JoystickScaler getLeftScaler() {
        return leftScaler;
    }

    /**
     * Gets the right joystick scaler
     *
     * @return {@code JoystickScaler} of the right <code>Joystick</code>
     */
    public JoystickScaler getRightScaler() {
        return rightScaler;
    }

    /**
     * Gets the D-Pad of this controller
     *
     * @return {@link org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad.Dpad} of this controller
     * @deprecated see {@link #dpad()}
     */
    @Deprecated
    public Dpad getDpad() {
        return dpad;
    }

    public Dpad dpad() {
        return dpad;
    }

    public ExtensibleGamepad when(Joystick joystick, Range<Double> is, RobotAction action, Axis axis) {
        handlers.add(new GamepadEvent(joystick, is, action, axis));
        return this;
    }

    public ExtensibleGamepad when(GamepadButton button, boolean is, RobotAction action) {
        handlers.add(new GamepadEvent(button, is, action));
        return this;
    }

    public ExtensibleGamepad when(GamepadTrigger trigger, Range<Double> is, RobotAction action) {
        handlers.add(new GamepadEvent(trigger, is, action));
        return this;
    }

    private boolean checkForExecuteGamepadCallback(Object o, Object value, Object opt) {
        if (o instanceof Joystick && value instanceof Range && opt instanceof Axis) {
            Joystick joystick = (Joystick) o;
            if (opt == Joystick.Axises.X) {
                return ((Range<Double>) value).contains(joystick.x);
            } else if (opt == Joystick.Axises.Y) {
                return ((Range<Double>) value).contains(joystick.y);
            }

            return false;
        } else if (o instanceof Buttons && value instanceof Boolean) {
            Buttons button = (Buttons) o;
            boolean val = (boolean) value;
            switch (button) {
                case A:
                    return a == val;
                case B:
                    return b == val;
                case X:
                    return x == val;
                case Y:
                    return y == val;
                case BACK:
                    return back == val;
                case GUIDE:
                    return guide == val;
                case START:
                    return start == val;
                case LEFT_BUMPER:
                    return leftBumper == val;
                case RIGHT_BUMPER:
                    return rightBumper == val;
                case DPAD_UP:
                    return dpad.up == val;
                case DPAD_LEFT:
                    return dpad.left == val;
                case DPAD_DOWN:
                    return dpad.down == val;
                case DPAD_RIGHT:
                    return dpad.right == val;
                default:
                    return false;
            }
        } else if (o instanceof Triggers && value instanceof Range) {
            switch ((Triggers) o) {
                case LEFT_TRIGGER:
                    return ((Range<Double>) value).contains((double) leftTrigger);
                case RIGHT_TRIGGER:
                    return ((Range<Double>) value).contains((double) rightTrigger);
            }
        }

        return false;
    }

    /**
     * Starts a recording session for the gamepad, updates will only be made whenever {@link
     * #updateGamepad(RobotContext, Gamepad)} is called, and each call gets a new record
     *
     * @param name the unique and case-sensitive name of the Gamepad Record for later reference
     */
    public synchronized void startRecording(String name) {
        gamepadRecord = new GamepadRecord(checkNotNull(name));
        recording = true;
    }

    /**
     * Records the state of the gamepad at a specific interval, this method should only be
     * called after {@link #startRecording(String)} has been called and before {@link #stopRecording()}
     * has been called on this object
     */
    private synchronized void record() {
        GamepadState record = new GamepadState(System.nanoTime(), leftJoystick, rightJoystick, dpad,
                a, b, x, y, guide, start, back,
                leftBumper, rightBumper, leftTrigger, rightTrigger);
        gamepadRecord.addRecord(record);
    }

    /**
     * Stops the recording session and saves the recording session
     */
    public synchronized void stopRecording() {
        recording = false;
        try {
            gamepadRecord.save();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        gamepadRecord = null;
    }

    /**
     * Checks to see if the gamepad is being recorded
     *
     * @return {@code true} if the gamepad is being recorded, otherwise {@code false}
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Returns a list of available playback records
     *
     * @return the available playback records
     */
    public List<String> playbackRecords() {
        return new LinkedList<>(GamepadRecord.getMap().keySet());
    }

    /**
     * Checks to see if a given record exists
     *
     * @return {@code true} if the given record exists, otherwise false
     */
    public boolean hasRecord(String name) {
        return GamepadRecord.getMap().containsKey(name);
    }

    /**
     * Starts a playback for the specified gamepad recording, this can be called multiple times
     * without any effect
     *
     * @param name the name of the the gamepad record
     */
    public synchronized void startPlayback(String name) {
        Map<String, GamepadRecord> map = GamepadRecord.toNameMap(GamepadRecord.getAvailableRecords());
        if (map.containsKey(name)) {
            gamepadRecord = map.get(name);
            playingBack = true;
        } else {
            throw new IllegalArgumentException("Unknown name for playback: " + name);
        }
    }

    /**
     * Stops the playback of the gamepad
     */
    public synchronized void stopPlayback() {
        playingBack = false;
    }

    /**
     * Checks if the gamepad is playing back data, or is getting its data from elsewhere
     *
     * @return {@code true} if the gamepad is replaying a previously recorded session, otherwise
     * {@code false} is returned
     */
    public boolean isPlayingBack() {
        return playingBack;
    }

    /**
     * Checks if the gamepad data is being streamed from the driver station
     *
     * @return {@code true} if the data is coming from the driver station, otherwise {@code false}
     */
    public boolean isFromDriverGamepad() {
        return !isPlayingBack();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ExtensibleGamepad)) {
            return false;
        }
        ExtensibleGamepad s = (ExtensibleGamepad) o;
        return leftJoystick.equals(s.leftJoystick) &&
                rightJoystick.equals(s.rightJoystick) &&
                dpad.equals(s.dpad) &&
                a == s.a &&
                b == s.b &&
                x == s.x &&
                y == s.y &&
                guide == s.guide &&
                start == s.guide &&
                back == s.back &&
                leftBumper == s.leftBumper &&
                rightBumper == s.rightBumper &&
                leftTrigger == s.leftTrigger &&
                rightTrigger == s.rightTrigger;
    }

    /**
     * Closes the gamepad and release any system resources it holds.
     * <p/>
     * <p>Although only the first call has any effect, it is safe to call close multiple times on
     * the same object. This is more lenient than the overridden {@code AutoCloseable.close()},
     * which may be called at most once.
     */
    @Override
    public void close() throws IOException {
        if (!hasBeenClosed) {
            if (gamepadRecord != null) {
                gamepadRecord.save();
            }
        }

        hasBeenClosed = true;
    }

    @Override
    public int hashCode() {
        HashUtil hash = new HashUtil(65);
        hash.addFieldToHash(leftJoystick)
                .addFieldToHash(rightJoystick)
                .addFieldToHash(dpad)
                .addFieldToHash(a)
                .addFieldToHash(b)
                .addFieldToHash(x)
                .addFieldToHash(y)
                .addFieldToHash(guide)
                .addFieldToHash(start)
                .addFieldToHash(back)
                .addFieldToHash(leftBumper)
                .addFieldToHash(rightBumper)
                .addFieldToHash(leftTrigger)
                .addFieldToHash(rightTrigger);
        return hash.get();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "left joystick: %s right joystick: %s" +
                        " dpad: %s" +
                        " a: %s SimulatedUsbDevice: %s x: %s y: %s" +
                        " guide: %s back: %s" +
                        " left bumper: %s right bumper: %s" +
                        " left trigger: %s right trigger: %s",
                leftJoystick, rightJoystick, dpad,
                a, b, x, y, guide, back,
                leftBumper, rightBumper,
                leftTrigger, rightTrigger);
    }

    public enum Buttons implements GamepadButton {
        DPAD_UP, DPAD_RIGHT, DPAD_DOWN, DPAD_LEFT, A, B, X, Y,
        GUIDE, START, BACK, LEFT_BUMPER, RIGHT_BUMPER
    }

    public enum Triggers implements GamepadTrigger {
        LEFT_TRIGGER, RIGHT_TRIGGER
    }

    /**
     * The Joystick for use in {@link ExtensibleGamepad}. This is an object representative of the
     * data present in the joysticks, and the data that the FTC SDK can give
     *
     * @author David Sargent
     * @since 0.1
     */
    public static class Joystick {
        private double x;
        private double y;
        private boolean pressed;

        /**
         * Update the joystick to match the parameters given
         *
         * @param X         the value of X coordinates, should be between -1 and 1
         * @param Y         the value of Y coordinates, should be between -1 and 1
         * @param isPressed is the Joystick being pressed
         */
        void update(double X, double Y, boolean isPressed) {
            x = X;
            y = Y;
            pressed = isPressed;
        }

        /**
         * Gets the X (in Cartesian) of the Joystick. This should range between -1 and 1, but it is
         * not guaranteed to be so.
         *
         * @return the Cartesian X of the Joystick
         */
        public double X() {
            return x;
        }

        /**
         * Gets the Y (in Cartesian) of the Joystick. This should range between -1 and 1, but it is
         * not guaranteed to be so.
         *
         * @return the Cartesian Y-coordinate of the Joystick
         */
        public double Y() {
            return y;
        }

        /**
         * Gets the button within the Joystick
         *
         * @return is the Joystick button pressed
         */
        public boolean isPressed() {
            return pressed;
        }

        /**
         * Gets the current coordinates in a Cartesian plane
         *
         * @return the current state of the joystick in a Cartesian plane
         */
        public CartesianCoordinates cartesian() {
            return new CartesianCoordinates(x, y);
        }

        /**
         * Gets the current coordinates in a Polar plane
         *
         * @return the current state of the joystick in a Polar plane
         */
        public PolarCoordinates polar() {
            return new PolarCoordinates(cartesian());
        }

        @Override
        public int hashCode() {
            return HashUtil.create(8556).addFieldToHash(x).addFieldToHash(y).addFieldToHash(pressed).get();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Joystick)) {
                return false;
            }

            Joystick joystick = (Joystick) o;
            return x == joystick.x && y == joystick.y && pressed == joystick.pressed;
        }

        @Override
        public String toString() {
            return " x: " + x + " y:" + y + " pressed: " + pressed;
        }

        public enum Axises implements GamepadAxis {X, Y}
    }

    /**
     * An representation of a gamepad's D-Pad control
     *
     * @author David Sargent
     * @since 0.1.0
     */
    public static class Dpad {
        private boolean up;
        private boolean down;
        private boolean right;
        private boolean left;

        /**
         * Updates the D-Pad based on the Gamepad's current status
         *
         * @param upPressed    is the Up button pressed
         * @param downPressed  is the Down button pressed
         * @param rightPressed is the right button pressed
         * @param leftPressed  is the left button pressed
         */
        void update(boolean upPressed, boolean downPressed,
                    boolean rightPressed, boolean leftPressed) {
            up = upPressed;
            down = downPressed;
            right = rightPressed;
            left = leftPressed;
        }

        /**
         * Gets the status of the D-Pad Up button
         *
         * @return is the Dpad Up button pressed
         */
        public boolean isUpPressed() {
            return up;
        }

        /**
         * Gets the status of the D-Pad Down button
         *
         * @return is the D-Pad Down button pressed
         */
        public boolean isDownPressed() {
            return down;
        }

        /**
         * Gets the status of the D-Pad Right button
         *
         * @return is the D-Pad Right pressed
         */
        public boolean isRightPressed() {
            return right;
        }

        /**
         * Gets the status of the D-Pad Left button
         *
         * @return is the D-Pad Left pressed
         */
        public boolean isLeftPressed() {
            return left;
        }

        @Override
        public int hashCode() {
            return HashUtil.create(149)
                    .addFieldToHash(up)
                    .addFieldToHash(down)
                    .addFieldToHash(left)
                    .addFieldToHash(right).get();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Dpad)) {
                return false;
            }

            Dpad joystick = (Dpad) o;
            return up == joystick.up && down == joystick.down &&
                    left == joystick.left && right == joystick.right;
        }

        @Override
        public String toString() {
            return "up: " + up + " down:" + down + " left: " + left + " right: " + right;
        }
    }

    public static class PlainJoystickScaler implements JoystickScaler {
        @Override
        public double scaleX(ExtensibleGamepad gamepad, double x) {
            return x;
        }

        @Override
        public double scaleY(ExtensibleGamepad gamepad, double y) {
            return y;
        }

        @Override
        public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
            return 0;
        }

        @Override
        public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
            return 0;
        }
    }

}
