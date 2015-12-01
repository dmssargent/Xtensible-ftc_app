package org.ftc.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;

@Disabled
@TeleOp
public class xTest extends ExtensibleOpMode {
    DcMotor motor1;
    DcMotor motor2;

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        motor1 = hardwareMap().dcMotors().get("motor1");
        motor2 = hardwareMap().dcMotors().get("motor2");
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        motor1.setPower(gamepad1().leftJoystick().Y());
        motor2.setPower(gamepad1().rightJoystick().Y());
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }
}
