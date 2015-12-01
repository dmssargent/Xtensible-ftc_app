package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
import org.ftccommunity.ftcxtensible.math.PolarCoordinates;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by First on 10/22/2015.
 */
@TeleOp
public class Test extends OpMode {
    DcMotor right0;
    DcMotor right1;
    DcMotor left0;
    DcMotor left1;

    @Override
    public void init() {
        right0 = hardwareMap.dcMotor.get("motor0");
        right1 = hardwareMap.dcMotor.get("motor1");

        left0 = hardwareMap.dcMotor.get("motor2");
        left1 = hardwareMap.dcMotor.get("motor3");


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

        telemetry.addData("Left:", leftPower);

        telemetry.addData("Right:", rightPower);
    }
}
