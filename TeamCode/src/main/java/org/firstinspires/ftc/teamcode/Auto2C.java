package org.firstinspires.ftc.teamcode;

//import com.qualcomm.ftccommon.configuration.EditPortListSpinnerActivity;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;

@Autonomous
public class Auto2C extends LinearOpMode {
    static final String RED = "RED";
    static final String BLUE = "BLUE";
    String color;

    @Override
    public void runOpMode() throws InterruptedException {

        ClutchHardware hardware = new ClutchHardware(gamepad1, hardwareMap);

        ExtensibleGamepad xGamepad1 = new ExtensibleGamepad(gamepad1);
        JoystickQuestions questions = new JoystickQuestions(xGamepad1, telemetry);
        questions.addQuestion("FOO", "What color?", RED, BLUE);
        while (!isStarted() && !Thread.currentThread().isInterrupted()) {
            xGamepad1.updateGamepad(gamepad1);
            questions.loop();
            telemetry.update();
        }
        waitForStart();
        questions.stop();
        color = questions.responseTo("FOO") == null ? "RED" : questions.responseTo("FOO");
        ElapsedTime time = new ElapsedTime();
        while (opModeIsActive()) {
            double rawLightDetected = hardware.opticalDistanceSensor.getRawLightDetected();
            telemetry.addData("ODS_DATA", rawLightDetected);
            telemetry.update();

            if (rawLightDetected > .5) {
                break;
            }
            if (time.seconds() > 5) {
                hardware.driveTrain.updateTarget(0, 0, 0);
            } else if (time.seconds() > 2.5)
                hardware.driveTrain.updateTarget(0, .18, 0);
            else if (time.seconds() > 2)
                hardware.driveTrain.updateTarget(0, .5, 0);
            else if (time.seconds() > 1)
                hardware.driveTrain.updateTarget(0, .7, 0);
            else
                hardware.driveTrain.updateTarget(0, 1, 0);
        }
        telemetry.addData("STAGE", 2).setRetained(true);
        while (opModeIsActive()) {
            double rawLightDetected = hardware.opticalDistanceSensor.getRawLightDetected();
            telemetry.addData("ODS_DATA", rawLightDetected);
            telemetry.update();

            if (hardware.distanceSensor.getDistance(DistanceUnit.CM) < 8) {
                break;
            }
            if (rawLightDetected > .6) {
                hardware.driveTrain.updateTarget(0, .2, 0);
            } else {
                hardware.driveTrain.updateTarget(0, 0, -.5);
            }
        }
        telemetry.update();
        time = new ElapsedTime();
        while (opModeIsActive()) {
            // left is red
            if (time.seconds() < 2) {
                if (hardware.colorSensor.red() > hardware.colorSensor.blue()) {
                    if (color == RED) {
                        hardware.pressed.setPosition(.3);
                    } else {
                        hardware.pressed.setPosition(.7);
                    }
                } else {
                    if (color == RED) {
                        hardware.pressed.setPosition(.7);
                    } else {
                        hardware.pressed.setPosition(.3);
                    }
                }
            } else {
                break;
            }
        }
        while (opModeIsActive()) {
            hardware.driveTrain.updateTarget(.2, 0, 0);
            if (time.seconds() > 4) {
                if (hardware.opticalDistanceSensor.getRawLightDetected() > .6) {
                    break;
                }
            }
        }
        hardware.driveTrain.updateTarget(0, 0, 0);
    }
}
