package org.ftccommunity.xtensible.xsimplify;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;
import java.util.Map;

public abstract class StandardOpMode extends ExtensibleOpMode {
    protected LinkedList<Object> storage;

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        for (Map.Entry<String, DcMotor> motorEntry : hardwareMap().dcMotors().entrySet()) {
            motorEntry.getValue().setPower(0d);
        }
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {
        if (in != null) {
            storage.add(in);
        }
    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }
}
