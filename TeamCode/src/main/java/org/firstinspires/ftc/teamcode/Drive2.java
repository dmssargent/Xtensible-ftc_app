package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensorImpl;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
public class Drive2 extends SimpleOpMode {
    //private Navigation navigation;
    //private DriveTrain driveTrain;
    //private OpticalDistanceSensor opticalDistanceSensor;
    //private ModernRoboticsI2cRangeSensor distanceSensor;
    //private DcMotor winch;
    //private Servo pressed;
    //private Servo ballLift;
    private double buttonPresserPos;
    private int ballLiftPos;
    private boolean ballLiftTriggerPressed;
    private boolean buttonPresserTriggerPressed;
    //private ColorSensor colorSensor;
    private ClutchHardware hardware;

    @Override
    public void init(RobotContext ctx) throws Exception {
        hardware = new ClutchHardware(this);
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
                buttonPresserPos = .65;
            } else if (gamepad1.isBPressed()) {
                buttonPresserPos = .35;
            }

            //buttonPresserPos %= 100;
        }

        buttonPresserTriggerPressed = gamepad1.isAPressed() || gamepad1.isBPressed();
        hardware.pressed.setPosition(buttonPresserPos);
        telemetry.addData("PRESSED_POS", buttonPresserPos);
    }

    private void winchControl() {
        if (gamepad1.dpad.isUpPressed()) {
            hardware.winch.setPower(-1);
        } else if (gamepad1.dpad.isDownPressed()) {
            hardware.winch.setPower(.1);
        } else {
            hardware.winch.setPower(-0.03);
        }
    }
}
