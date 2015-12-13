package org.ftc.opmodes;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.bindings.DataBinder;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.handlers.RobotUncaughtExceptionHandler;

//import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
//import org.ftccommunity.ftcxtensible.math.PolarCoordinates;
//import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by First on 10/22/2015.
 */
@TeleOp
public class TeamClutchTeleop extends OpMode {
    private DcMotor right0;
    private DcMotor right1;
    private DcMotor left0;
    private DcMotor left1;


    private DcMotor armLift;
    private DcMotor armWrench;
    private DcMotor winchM;
    private DcMotor hookGrabber;

    private double wrenchSpeed;
    private double armSpeed;
    private double armLiftS;

    @Override
    public void init() {
        Thread.currentThread().setPriority(8);

        // Build an exception handler
        int id = DataBinder.getInstance().integers().get(DataBinder.RC_VIEW);
                ((Activity) hardwareMap.appContext).findViewById(id).post(new Runnable() {
            @Override
            public void run() {
                Activity controller = (Activity) hardwareMap.appContext;
                @SuppressWarnings("ResourceType") PendingIntent intent = PendingIntent.getActivity(controller.getBaseContext(), 0,
                        new Intent(controller.getIntent()), controller.getIntent().getFlags());
                Thread.currentThread().setUncaughtExceptionHandler(new RobotUncaughtExceptionHandler(hardwareMap.appContext, intent, 250));
            }
        });


        right0 = hardwareMap.dcMotor.get("motor0");
        right1 = hardwareMap.dcMotor.get("motor1");
        left0 = hardwareMap.dcMotor.get("motor2");
        left1 = hardwareMap.dcMotor.get("motor3");

        armWrench = hardwareMap.dcMotor.get("armWrench");
        armLift = hardwareMap.dcMotor.get("armLift");
        winchM = hardwareMap.dcMotor.get("winchM");
        hookGrabber = hardwareMap.dcMotor.get("hookGrabber");

        wrenchSpeed = .8;
        armSpeed = .6;
        armLiftS = .4;


        right0.setDirection(DcMotor.Direction.REVERSE);
        right1.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
    /*    PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(gamepad1.left_stick_x, gamepad1.left_stick_y));

        double motor1Power = Range.clip(coordinates.getR() * Math.sin(coordinates.getTheta() + (Math.PI / 4)) + gamepad1.right_stick_x, -1,1);
        double motor2Power = Range.clip(coordinates.getR() * Math.cos(coordinates.getTheta() + (Math.PI / 4)) - gamepad1.right_stick_x, -1, 1);
        double motor3Power = Range.clip(coordinates.getR() * Math.cos(coordinates.getTheta() + (Math.PI / 4)) + gamepad1.right_stick_x, -1, 1);
        double motor4Power = Range.clip(coordinates.getR() * Math.sin(coordinates.getTheta() + (Math.PI / 4)) - gamepad1.right_stick_x, -1, 1);

        right0.setPower(motor1Power);
        left0.setPower(motor2Power);
        right1.setPower(motor3Power);
        left1.setPower(motor4Power);

        telemetry.addData("MEC::", String.format("M1: %s; M2: %s; M3: %s; M4: %s", motor1Power, motor2Power, motor3Power, motor4Power));*/
        double rightPower = gamepad1.right_bumper ?  gamepad1.right_stick_y : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.right_stick_y), 3), -1, 1);
        right0.setPower(rightPower);
        right1.setPower(rightPower);

        double leftPower = gamepad1.right_bumper ? gamepad1.left_stick_y  : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.left_stick_y), 3), -1, 1);
        left0.setPower(leftPower);
        left1.setPower(leftPower);

        double currentArmMotorSpeed = 0;
        if (gamepad1.dpad_right && !gamepad1.dpad_left) {
            currentArmMotorSpeed = -armLiftS;
        } else if (gamepad1.dpad_left && !gamepad1.dpad_right) {
            currentArmMotorSpeed = armLiftS;
        }

        double currentWrenchSpeed = 0;
        if (gamepad1.dpad_down && !gamepad1.dpad_up) {
            currentWrenchSpeed = wrenchSpeed;
        } else if (gamepad1.dpad_up && !gamepad1.dpad_down) {
            currentWrenchSpeed = -wrenchSpeed;
        }

        if (gamepad1.x) {
            wrenchSpeed += .01;
            if (wrenchSpeed > 1) {
                wrenchSpeed = -1;
            }
        }

        if (gamepad1.a) {
            armSpeed += .01;
            if (armSpeed > 1) {
                armSpeed = -1;
            }
        }

        if (gamepad1.y) {
            winchM.setPower(-wrenchSpeed);
        } else if (gamepad1.b) {
            winchM.setPower(wrenchSpeed);
        } else {
            winchM.setPower(0);
        }


        if (gamepad1.right_trigger > .25) {
            hookGrabber.setPower(.1);
        } else if (gamepad1.left_trigger > .25) {
            hookGrabber.setPower(-.1);
        } else {
            hookGrabber.setPower(0);
        }

        armLift.setPower(currentArmMotorSpeed);
        armWrench.setPower(currentWrenchSpeed);

        telemetry.addData("Arm Power:", "Wrench: " + currentWrenchSpeed + " Arm: " + currentArmMotorSpeed);
        telemetry.addData("Left:", leftPower);
        telemetry.addData("Right:", rightPower);
    }
}
