package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
public class Drive2 extends SimpleOpMode {
    private double buttonPresserPos;
    private int ballLiftPos;
    private boolean ballLiftTriggerPressed;
    private boolean buttonPresserTriggerPressed;
    private ClutchHardware hardware;

    @Override
    public void init(RobotContext ctx) throws Exception {
        hardware = new ClutchHardware(this);
        //hardware.winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //hardware.winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        hardware.driveTrain.updateTargetWithGamepad();
        winchControl();
        buttonPresserControl();
        ballLiftControl();
        sensorTelemetry();
    }

    private void sensorTelemetry() {
        try {
            telemetry.data("ODS_READ", hardware.opticalDistanceSensor == null ? "No Sensor " : hardware.opticalDistanceSensor.getRawLightDetected());
            telemetry.data("DIS_READ", hardware.distanceSensor == null ? "No Sensor" : hardware.distanceSensor.getDistance(DistanceUnit.CM));
            telemetry.data("COLOR", hardware.colorSensor == null ? "No Sensor" : (hardware.colorSensor.red() > hardware.colorSensor.blue() ? "Red" : "Blue"));
            telemetry.data("MOTOR_LOCK", hardware.winch.getMode() == DcMotor.RunMode.RUN_USING_ENCODER ? "unlock" : "lock");

            DriveTrain driveTrain = hardware.driveTrain;
            telemetry.data("THETA", driveTrain.getThetaInDegrees());
            telemetry.data("READING", driveTrain.getLeftMotorReading() + ", " + driveTrain.getRightMotorReading());
            //telemetry.data("MOTOR_ENC", hardware.winch.getCurrentPosition());
        } catch (NullPointerException ignored) {
        }
    }

    private void ballLiftControl() {
        if (!ballLiftTriggerPressed) {
            if (gamepad1.isXPressed()) {
                ballLiftPos += 1;
            } else if (gamepad1.isYPressed()) {
                ballLiftPos -= 1;
            }
            ballLiftPos %= 100;
        }
        ballLiftTriggerPressed = gamepad1.isXPressed() || gamepad1.isYPressed();
        hardware.ballLift.setPosition(ballLiftPos / 100d);
        telemetry.addData("BALL_LIFT_POS", ballLiftPos);
    }

    private void buttonPresserControl() {
        if (!buttonPresserTriggerPressed) {
            if (gamepad1.isAPressed()) {
                //buttonPresserPos += 5;
                buttonPresserPos = .70;
            } else if (gamepad1.isBPressed()) {
                buttonPresserPos = .30;
            }

            //buttonPresserPos %= 100;
        }

        buttonPresserTriggerPressed = gamepad1.isAPressed() || gamepad1.isBPressed();
        hardware.pressed.setPosition(buttonPresserPos);
        telemetry.addData("PRESSED_POS", buttonPresserPos);
    }

    private void winchControl() {
        DcMotor winch = hardware.winch;
        if (gamepad1.dpad.isUpPressed()) {
            //if (winch.getMode() == DcMotor.RunMode.RUN_USING_ENCODER)
            winch.setPower(-1);
            //winch.setTargetPosition(winch.getCurrentPosition() - 50);
        } else if (gamepad1.dpad.isDownPressed()) {
            //if (winch.getMode() == DcMotor.RunMode.RUN_USING_ENCODER)
            winch.setPower(.1);
            //else winch.setTargetPosition(winch.getCurrentPosition() + 50);
        } else {
            winch.setPower(-0.03);
        }

//        if (gamepad1.isLeftBumperPressed()) {
//            if (winch.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
//                winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//                winch.setPower(0.02);
//            }
//
//            if (winch.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
//                int currPosition = winch.getCurrentPosition();
//                winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                winch.setTargetPosition(currPosition);
//                winch.setPower(-0.03);
//            }
//        }
    }
}
