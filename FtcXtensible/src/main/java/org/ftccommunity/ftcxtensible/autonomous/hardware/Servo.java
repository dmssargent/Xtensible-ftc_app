/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.ftccommunity.ftcxtensible.internal.Alpha;

@Deprecated
@Alpha
public class Servo {
    final HardwareMap hardwareMap = new HardwareMap();
    private com.qualcomm.robotcore.hardware.Servo me;

    private double position;
    private String hw_name;
    private String common_name;

    public Servo(String motor_name) {
        hw_name = motor_name;
        me = hardwareMap.servo.get(hw_name);

        common_name = motor_name;
        position = 0d;
    }

    public Servo(String hardware_name, String name) {
        hw_name = hardware_name;
        common_name = name;
        position = 0d;
    }

    public String GetHWName() {
        return hw_name;
    }

    public String GetName() {
        return common_name;
    }

    public double GetPosition() {
        return position;
    }

    public void SetPosition(int motor_speed) {
        position = motor_speed;
    }

    public void SetPositionHW() {
        me.setPosition(position);
    }
}
