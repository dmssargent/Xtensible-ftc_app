package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by First on 10/22/2015.
 */
@TeleOp(name = "Teleop")
public class TeamClutchTeleop extends OpMode {
    private DcMotor rightRear;
    private DcMotor rightFront;
    private DcMotor leftRear;
    private DcMotor leftFront;


    private DcMotor armPivot;
    private DcMotor armLift;
    private DcMotor winch;
    //private DcMotor hookGrabber;

    private double winchSpeed;
    private double armRotateSpeed;
    private double armLiftSpeed;
    @Override
    public void init() {
        rightRear = hardwareMap.dcMotor.get("rightRear");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftRear = hardwareMap.dcMotor.get("leftRear");
        leftFront = hardwareMap.dcMotor.get("leftFront");

        armLift = hardwareMap.dcMotor.get("armLift");
        armPivot = hardwareMap.dcMotor.get("armPivot");
        winch = hardwareMap.dcMotor.get("winchMotor");
        //hookGrabber = hardwareMap.dcMotor.get("hookGrabber");

        winchSpeed = 1;
        armRotateSpeed = .2;
        armLiftSpeed = .4;

        rightRear.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
    /*    PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(gamepad1.left_stick_x, gamepad1.left_stick_y));

        double motor1Power = Range.clip(coordinates.getR() * Math.sin(coordinates.getTheta() + (Math.PI / 4)) + gamepad1.right_stick_x, -1,1);
        double motor2Power = Range.clip(coordinates.getR() * Math.cos(coordinates.getTheta() + (Math.PI / 4)) - gamepad1.right_stick_x, -1, 1);
        double motor3Power = Range.clip(coordinates.getR() * Math.cos(coordinates.getTheta() + (Math.PI / 4)) + gamepad1.right_stick_x, -1, 1);
        double motor4Power = Range.clip(coordinates.getR() * Math.sin(coordinates.getTheta() + (Math.PI / 4)) - gamepad1.right_stick_x, -1, 1);

        rightRear.setPower(motor1Power);
        leftRear.setPower(motor2Power);
        rightFront.setPower(motor3Power);
        leftFront.setPower(motor4Power);

        telemetry.addData("MEC::", String.format("M1: %s; M2: %s; M3: %s; M4: %s", motor1Power, motor2Power, motor3Power, motor4Power));*/
        double rightPower = gamepad1.right_bumper ?  gamepad1.right_stick_y : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.right_stick_y), 3), -1, 1);
        rightRear.setPower(rightPower);
        rightFront.setPower(rightPower);

        double leftPower = gamepad1.right_bumper ? gamepad1.left_stick_y  : Range.clip(1.68 * Math.pow(Math.tanh(gamepad1.left_stick_y), 3), -1, 1);
        leftRear.setPower(leftPower);
        leftFront.setPower(leftPower);

        double currentArmMotorSpeed = 0;
        if (gamepad1.dpad_right && !gamepad1.dpad_left) {
            currentArmMotorSpeed = -armLiftSpeed;
        } else if (gamepad1.dpad_left && !gamepad1.dpad_right) {
            currentArmMotorSpeed = armLiftSpeed;
        }

        double currentWrenchSpeed = 0;
        if (gamepad1.dpad_down && !gamepad1.dpad_up) {
            currentWrenchSpeed = winchSpeed;
        } else if (gamepad1.dpad_up && !gamepad1.dpad_down) {
            currentWrenchSpeed = -winchSpeed;
        }

       /* if (gamepad1.x) {
            winchSpeed += .01;
            if (winchSpeed > 1) {
                winchSpeed = -1;
            }
        }

        if (gamepad1.a) {
            armRotateSpeed += .01;
            if (armRotateSpeed > 1) {
                armRotateSpeed = -1;
            }
        }*/

        if (gamepad1.y) {
            winch.setPower(-winchSpeed);
        } else if (gamepad1.b) {
            winch.setPower(winchSpeed);
        } else {
            winch.setPower(0);
        }


        /*if (gamepad1.right_trigger > .25) {
            hookGrabber.setPower(.1);
        } else if (gamepad1.left_trigger > .25) {
            hookGrabber.setPower(-.1);
        } else {
            hookGrabber.setPower(0);
        }*/

        armPivot.setPower(currentArmMotorSpeed);
        armLift.setPower(currentWrenchSpeed);

        telemetry.addData("Arm Power:", "Wrench: " + currentWrenchSpeed + " Arm: " + currentArmMotorSpeed);
        telemetry.addData("Left:", leftPower);
        telemetry.addData("Right:", rightPower);
    }
}
