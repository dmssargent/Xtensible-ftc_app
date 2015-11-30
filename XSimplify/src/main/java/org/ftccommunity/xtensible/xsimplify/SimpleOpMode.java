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

package org.ftccommunity.xtensible.xsimplify;


import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.robot.ExtensibleTelemetry;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class SimpleOpMode extends StandardOpMode {
    protected ExtensibleHardwareMap hardwareMap;
    protected ExtensibleGamepad gamepad1;
    protected ExtensibleGamepad gamepad2;
    protected ExtensibleTelemetry telemetry;
    Class<? extends SimpleOpMode> childOpMode;
    SimpleOpMode child;

    public SimpleOpMode(SimpleOpMode child) {
        childOpMode = child.getClass();
        this.child = child;
    }

    public SimpleOpMode() {
        super();
    }

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        hardwareMap = hardwareMap();
        gamepad1 = gamepad1();
        gamepad2 = gamepad2();
        telemetry = telemetry();

        if (child != null && childOpMode != null) {
            LinkedList<Field> fields = new LinkedList<>(Arrays.asList(childOpMode.getFields()));
            for (Field field : fields) {
                ExtensibleHardwareMap.DeviceMap map = getFromClass(field.getType());
                if (map == null) {
                    continue;
                }

                if (map.containsKey(field.getName())) {
                    field.set(child, map.get(field.getName()));
                }
            }
        }

        init(ctx);
    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        loop(ctx);
    }

    public ExtensibleHardwareMap.DeviceMap<String, DcMotor> getFromClass(Class klazz) {
        if (klazz == DcMotor.class) {
            return hardwareMap().dcMotors();
        }

        return null;
    }

    public abstract void init(RobotContext ctx) throws Exception;

    public abstract void loop(RobotContext ctx) throws Exception;
}
