package org.ftc.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.xtensible.xsimplify.SimpleOpMode;

import java.util.LinkedList;

/**
 * Created by first on 2/10/16.
 */
public class TeamClutchDrive extends SimpleOpMode {
    protected DcMotor right0;
    protected DcMotor right1;
    protected DcMotor left0;
    protected DcMotor left1;

    protected DcMotor armLift;
    protected DcMotor armWrench;

    protected double ARM_MOTOR_BASE = 0.50;
    protected DcMotor armPivot;
    protected Servo climbingMenHolder;
    protected double climbingMenHolderPosition = .75;

    @Override
    public void init(RobotContext ctx) throws Exception {
        robotInit();
    }

    protected void robotInit() {
        right0 = hardwareMap.dcMotors().get("right0");
        right1 = hardwareMap.dcMotors().get("right1");

        left0 = hardwareMap.dcMotors().get("left0");
        left1 = hardwareMap.dcMotors().get("left1");

        armLift = hardwareMap.dcMotors().get("armLift");
        armWrench = hardwareMap.dcMotors().get("armWinch");
        armPivot = hardwareMap.dcMotors().get("armPivot");

        climbingMenHolder = hardwareMap.servos().get("menHolder");


        right0.setDirection(DcMotor.Direction.REVERSE);
        right1.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        robotDrive();
    }

    protected void robotDrive() {
        double rightPower = gamepad1.isRightBumperPressed() ? gamepad1.rightJoystick().Y() : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.rightJoystick().Y()), 3), -1, 1);
        right0.setPower(rightPower);
        right1.setPower(rightPower);

        double leftPower = gamepad1.isRightBumperPressed() ? gamepad1.leftJoystick().Y() : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.leftJoystick().Y()), 3), -1, 1);
        left0.setPower(leftPower);
        left1.setPower(leftPower);

        double wrenchPower = gamepad1.getDpad().isUpPressed() ? ARM_MOTOR_BASE : 0;
        wrenchPower = gamepad1.getDpad().isDownPressed() ? -ARM_MOTOR_BASE : wrenchPower;
        //wrenchPower = gamepad1.dpad_up ? (wrenchPower != 0 ? ARM_MOTOR_BASE : 0) : 0;
        armWrench.setPower(wrenchPower);

        double armPower = gamepad1.getDpad().isRightPressed() ? ARM_MOTOR_BASE : 0;
        armPower = gamepad1.getDpad().isLeftPressed() ? -ARM_MOTOR_BASE : armPower;
        //armPower = gamepad1.dpad_up ? (armPower != 0 ? ARM_MOTOR_BASE : 0) : 0;


        if (pivotInWriteMode()) {
            armLift.setPower(armPower);

            if (gamepad1.isXPressed()) {
                armPivot.setPower(ARM_MOTOR_BASE / 2);
            } else if (gamepad1.isYPressed()) {
                armPivot.setPower(-ARM_MOTOR_BASE / 2);
            } else {
                armPivot.setPower(0);
            }
        }

        final boolean positionChanged;
        if (gamepad1.isAPressed()) {
            positionChanged = true;
            climbingMenHolderPosition = .65;
        } else if (gamepad1.isBPressed()) {
            positionChanged = true;
            climbingMenHolderPosition = .25;
        } else {
            positionChanged = false;
        }

        if (positionChanged || getLoopCount() == 1) {
            climbingMenHolder.setPosition(climbingMenHolderPosition);
        }

        telemetry.data("Left:", leftPower);
        telemetry.data("Right:", rightPower);
        telemetry.data("Arm Power:", ARM_MOTOR_BASE + " " + armPower + " " + wrenchPower);
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> objects) throws Exception {
        robotStop(ctx, objects);
    }

    protected void robotStop(RobotContext ctx, LinkedList<Object> objects) throws Exception {
        super.stop(ctx, objects);
    }

    protected boolean pivotInWriteMode() {
        return armPivot.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY;
    }
}
