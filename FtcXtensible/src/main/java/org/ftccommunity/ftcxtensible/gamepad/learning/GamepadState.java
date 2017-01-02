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

package org.ftccommunity.ftcxtensible.gamepad.learning;

import org.ftccommunity.ftcxtensible.core.internal.HashUtil;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class GamepadState {
    public final long timestamp;
    public final ExtensibleGamepad.Joystick leftJoystick;
    public final ExtensibleGamepad.Joystick rightJoystick;
    public final ExtensibleGamepad.Dpad dpad;
    public final boolean a;
    public final boolean b;
    public final boolean x;
    public final boolean y;
    public final boolean guide;
    public final boolean start;
    public final boolean back;
    public final boolean leftBumper;
    public final boolean rightBumper;
    public final float leftTrigger;
    public final float rightTrigger;

    private transient int hashcode = 0;

    public GamepadState(long timestamp, ExtensibleGamepad.Joystick leftJoystick, ExtensibleGamepad.Joystick rightJoystick,
                        ExtensibleGamepad.Dpad dpad, boolean a, boolean b, boolean x, boolean y,
                        boolean guide, boolean start, boolean back,
                        boolean leftBumper, boolean rightBumper, float leftTrigger, float rightTrigger) {
        this.timestamp = timestamp;
        this.leftJoystick = leftJoystick;
        this.rightJoystick = rightJoystick;
        this.dpad = dpad;
        this.a = a;
        this.b = b;
        this.x = x;
        this.y = y;
        this.guide = guide;
        this.start = start;
        this.back = back;
        this.leftBumper = leftBumper;
        this.rightBumper = rightBumper;
        this.leftTrigger = leftTrigger;
        this.rightTrigger = rightTrigger;
    }

    @Override
    public int hashCode() {
        if (hashcode != 0) {
            return hashcode;
        }

        HashUtil hash = new HashUtil(3546);
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
        hashcode = hash.get();
        return hashcode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof GamepadState)) {
            return false;
        }
        GamepadState s = (GamepadState) o;
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

    public String compressedString() {
        return String.format(Locale.ENGLISH, "%h:%e,%e,%SimulatedUsbDevice,%e,%e,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%e,%e;", hashCode(), leftJoystick.X(), leftJoystick.Y(), leftJoystick.isPressed(), rightJoystick.X(), rightJoystick.Y(), rightJoystick.isPressed(),
                dpad.isUpPressed(), dpad.isRightPressed(), dpad.isDownPressed(), dpad.isLeftPressed(), a, b, x, y,
                guide, start, back,
                leftBumper, rightBumper, leftTrigger, rightTrigger);
    }

    public String compressedString(@Nullable GamepadState lastState) {
        if (lastState == null) {
            return compressedString();
        }

        String temp = "";
        if (lastState.hashCode() == hashCode()) {
            temp += (";");
        } else {
            temp += (String.format("%h:", hashCode()));
            if (leftJoystick.equals(lastState.leftJoystick)) {
                temp += (",,,");
            } else {
                temp += (formatJoystickSave(leftJoystick));
            }

            if (rightJoystick.equals(lastState.rightJoystick)) {
                temp += (",,,");
            } else {
                temp += (formatJoystickSave(rightJoystick));
            }

            if (dpad.equals(lastState.dpad)) {
                temp += (",,,,");
            } else {
                temp += (formatDpadSave(dpad));
            }

            temp += (formatButtonSave(a, lastState.a));
            temp += (formatButtonSave(b, lastState.b));
            temp += (formatButtonSave(x, lastState.x));
            temp += (formatButtonSave(y, lastState.y));
            temp += (formatButtonSave(start, lastState.start));
            temp += (formatButtonSave(back, lastState.back));
            temp += (formatButtonSave(guide, lastState.guide));
            temp += (formatButtonSave(leftBumper, lastState.leftBumper));
            temp += (formatButtonSave(rightBumper, lastState.rightBumper));
            temp += (formatTriggerSave(leftTrigger, lastState.leftTrigger));
            temp += (formatTriggerSave(rightTrigger, lastState.rightTrigger));

        }

        return temp;
    }

    private String formatJoystickSave(ExtensibleGamepad.Joystick state) {
        return String.format(Locale.ENGLISH, "%e,%e,%SimulatedUsbDevice,", state.X(), state.Y(), state.isPressed());
    }

    // // TODO: 8/11/2016  
//    private ExtensibleGamepad.Joystick parseJoystickSave(String data) {
//
//    }

    private String formatDpadSave(ExtensibleGamepad.Dpad dpad) {
        return String.format(Locale.ENGLISH, "%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice,%SimulatedUsbDevice", dpad.isUpPressed(), dpad.isDownPressed(), dpad.isRightPressed(), dpad.isLeftPressed());
    }

    private String formatButtonSave(boolean present, boolean old) {
        if (present == old) {
            return ",";
        } else {
            return (present ? "1" : "0") + ",";
        }
    }

    private String formatTriggerSave(double trigger, double lastTrigger) {
        if (trigger == lastTrigger) {
            return "";
        } else {
            return String.valueOf(trigger);
        }
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
}
