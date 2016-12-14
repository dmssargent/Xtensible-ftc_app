package org.firstinspires.ftc.teamcode;

//import com.qualcomm.ftccommon.configuration.EditPortListSpinnerActivity;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;

@Autonomous
public class Auto2B extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        ClutchHardware hardware = new ClutchHardware(gamepad1, hardwareMap);
        hardware.driveTrain.configureDriveSystem(DriveTrain.DriveSystems.HOLONOMIC);
        double rawLightDetected = hardware.opticalDistanceSensor.getRawLightDetected();


        ExtensibleGamepad xGamepad1 = new ExtensibleGamepad(gamepad1);
        JoystickQuestions questions = new JoystickQuestions(xGamepad1, telemetry);
        questions.addQuestion("FOO", "What delay?", "0", "5", "10", "15");
        while (!isStarted() && !Thread.currentThread().isInterrupted()) {
            xGamepad1.updateGamepad(gamepad1);
            questions.loop();
            telemetry.update();
        }
        waitForStart();
        questions.stop();
        String delayS = questions.responseTo("FOO") == null ? "0" : questions.responseTo("FOO");
        int delay = Integer.valueOf(delayS);
        sleep(delay * 1000);
        ElapsedTime time = new ElapsedTime();
        while (opModeIsActive()) {
            telemetry.addData("ODS_DATA", rawLightDetected);
            telemetry.update();

            if (time.seconds() > 4) {
                hardware.driveTrain.updateTarget(0, 0, 0);
                break;
            } else if (time.seconds() > 2.5)
                hardware.driveTrain.updateTarget(0, .18, 0);
            else if (time.seconds() > 2)
                hardware.driveTrain.updateTarget(0, .5, 0);
            else if (time.seconds() > 1)
                hardware.driveTrain.updateTarget(0, .7, 0);
            else
                hardware.driveTrain.updateTarget(0, 1, 0);
        }
    }
}
