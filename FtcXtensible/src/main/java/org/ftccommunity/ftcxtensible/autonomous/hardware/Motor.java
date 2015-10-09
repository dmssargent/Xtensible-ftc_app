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

package org.ftccommunity.ftcxtensible.autonomous.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.ftccommunity.ftcxtensible.autonomous.BasicHardware;
import org.ftccommunity.ftcxtensible.internal.Alpha;

@Deprecated
@Alpha
public class Motor implements BasicHardware {
    final HardwareMap hardwareMap = new HardwareMap();
    private DcMotor me;

    private int speed;
    private String hw_name;
    private String common_name;


    public Motor(String hardware_name) {
        hw_name = hardware_name;
        me = hardwareMap.dcMotor.get(hw_name);

        common_name = hardware_name;
        speed = 0;
    }

    public Motor(String hardware_name, String name) {
        hw_name = hardware_name;
        common_name = name;
        speed = 0;
    }

    public String GetName() {
        return common_name;
    }

    public String GetHWName() {
        return hw_name;
    }

    public int GetSpeed() {
        return speed;
    }

    public void SetSpeed(int motor_speed) {
        speed = motor_speed;
    }

    public void SetSpeedHW() {
        me.setPower(speed);
    }
}

