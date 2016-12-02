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
    private DriveTrain driveTrain;
    private OpticalDistanceSensor opticalDistanceSensor;
    private ModernRoboticsI2cRangeSensor distanceSensor;
    private DcMotor winch;
    private Servo pressed;
    private Servo ballLift;
    private double buttonPresserPos;
    private int ballLiftPos;
    private boolean ballLiftTriggerPressed;
    private boolean buttonPresserTriggerPressed;
    private ColorSensor colorSensor;

    @Override
    public void init(RobotContext ctx) throws Exception {
        //navigation = new Navigation(ctx.appContext());
//        leftFront = hardwareMap.dcMotor("leftFront");
//        leftRear = hardwareMap.dcMotor("leftRear");
//        rightFront = hardwareMap.dcMotor("rightFront");rd
//        rightRear = hardwareMap.dcMotor("rightRear");
        winch = hardwareMap.get("winch");
        pressed = hardwareMap.get("buttonPresser");
        ballLift = hardwareMap.get("ballLift");
        driveTrain = new DriveTrain(gamepad1, null, this, "leftFront", "rightFront", "leftRear", "rightRear");

        try {
            opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Optical Distance sensor; \"opticalDistance\"");
        }
        try {
            distanceSensor = legacyHardwareMap().get(ModernRoboticsI2cRangeSensor.class, "ultrasonic");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Ultrasonic sensor; \"ultrasonic\"");
            throw ex;
        }
        try {
            colorSensor = hardwareMap.colorSensors().get("colorSensor");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Cannot obtain Color Sensor");

        }
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        driveTrain.updateTargetWithGamepad();

        if (gamepad1.dpad.isUpPressed()) {
            winch.setPower(-1);
        } else if (gamepad1.dpad.isDownPressed()) {
            winch.setPower(.1);
        } else {
            winch.setPower(-0.05);
        }

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
        pressed.setPosition(buttonPresserPos);
        telemetry.addData("PRESSED_POS", buttonPresserPos);
        if (!ballLiftTriggerPressed) {
            if (gamepad1.isXPressed()) {
                ballLiftPos += 1;
            } else if (gamepad1.isYPressed()) {
                ballLiftPos -= 1;
            }
            ballLiftPos %= 100;
        }
        ballLiftTriggerPressed = gamepad1.isXPressed() || gamepad1.isYPressed();
        ballLift.setPosition(ballLiftPos / 100d);
        telemetry.addData("BALL_LIFT_POS", ballLiftPos);

        try {
            telemetry.data("ODS_READ", opticalDistanceSensor == null ? "No Sensor " : opticalDistanceSensor.getRawLightDetected());
            telemetry.data("DIS_READ", distanceSensor == null ? "No Sensor" : distanceSensor.getDistance(DistanceUnit.CM));
            telemetry.data("COLOR", colorSensor == null ? "No Sensor" : (colorSensor.red() > colorSensor.blue() ? "Red" : "Blue"));
        } catch (NullPointerException ignored) {
        }
    }
}
