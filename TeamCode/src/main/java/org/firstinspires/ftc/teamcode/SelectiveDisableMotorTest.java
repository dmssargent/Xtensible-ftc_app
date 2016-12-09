package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

/**
 * Test class to verify motor targets
 */
@TeleOp
public class SelectiveDisableMotorTest extends SimpleOpMode {
    private ClutchHardware hardware;

    @Override
    public void init(RobotContext ctx) throws Exception {
        hardware = new ClutchHardware(this);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        hardware.driveTrain.enableMotor(0, gamepad1.isAPressed());
        hardware.driveTrain.enableMotor(1, gamepad1.isBPressed());
        hardware.driveTrain.enableMotor(2, gamepad1.isXPressed());
        hardware.driveTrain.enableMotor(3, gamepad1.isYPressed());
        hardware.driveTrain.updateTarget(0, 1, 0);
    }
}
