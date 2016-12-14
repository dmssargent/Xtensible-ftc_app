package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

/**
 * Test class to verify motor targets
 */
@TeleOp
public class MecanumTuningMotor extends SimpleOpMode {
    private ClutchHardware hardware;
    private int motorSelectionIndex = -1;

    @Override
    public void init(RobotContext ctx) throws Exception {
        hardware = new ClutchHardware(this);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        if (gamepad1.isAPressed()) {
            motorSelectionIndex = 0;
        } else if (gamepad1.isBPressed()) {
            motorSelectionIndex = 1;
        } else if (gamepad1.isXPressed()) {
            motorSelectionIndex = 2;
        } else if (gamepad1.isYPressed()) {
            motorSelectionIndex = 3;
        } else if (gamepad1.isRightBumperPressed()) {
            motorSelectionIndex = -1;
        }

        double value = (-gamepad1.leftJoystick.Y() + 1) / 2d;
        //value = Range.scale(value, 0, 1, .35, 1);
        telemetry.addData("VAL", value);
        if (motorSelectionIndex >= 0) {
            hardware.driveTrain.tuneMotorInstance(motorSelectionIndex, value);
        }

        hardware.driveTrain.updateTarget(gamepad1.rightJoystick.X(), -gamepad1.rightJoystick.Y(), 0);

        for (int i = 0; i < 4; i++) {
            telemetry.addData(String.valueOf(i), "Tgrt Pow: " + hardware.driveTrain.motorTuningParameter(i));
        }
    }
}
