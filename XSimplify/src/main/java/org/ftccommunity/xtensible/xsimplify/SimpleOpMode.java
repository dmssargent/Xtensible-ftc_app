package org.ftccommunity.xtensible.xsimplify;


import com.google.common.collect.ImmutableMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

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
        if (child != null && childOpMode != null) {
            LinkedList<Field> fields = new LinkedList<>(Arrays.asList(childOpMode.getFields()));
            for (Field field : fields) {
                ImmutableMap map = getFromClass(field.getType());
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

    public ImmutableMap<String, ? extends HardwareDevice> getFromClass(Class klazz) {
        if (klazz == DcMotor.class) {
            return hardwareMap().dcMotors();
        }

        return null;
    }

    public abstract void init(RobotContext ctx);

    public abstract void loop(RobotContext ctx);
}
