package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.ftccommunity.ftcxtensible.robot.DriveTrain;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

/**
 * Test class to verify motor targets
 */
@TeleOp
@Disabled
public class SelectiveDisableMotorTest extends SimpleOpMode {
    private DriveTrain driveTrain;

    @Override
    public void init(RobotContext ctx) throws Exception {
        driveTrain = new DriveTrain(gamepad1, this, "leftFront", "rightFront", "leftRear", "rightRear");
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        driveTrain.enableMotor(0, gamepad1.isAPressed());
        driveTrain.enableMotor(1, gamepad1.isBPressed());
        driveTrain.enableMotor(2, gamepad1.isXPressed());
        driveTrain.enableMotor(3, gamepad1.isYPressed());
        driveTrain.updateTarget(0, 1, 0);
    }
}
