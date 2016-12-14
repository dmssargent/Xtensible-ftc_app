package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
@Disabled
public class FooTest extends SimpleOpMode {
    @Override
    public void init(RobotContext ctx) throws Exception {
        hardwareMap().dcMotors().get("trst");
        telemetry.data("INT", "t");
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {

    }
}
