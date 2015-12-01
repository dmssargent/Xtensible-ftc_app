package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by First on 10/29/2015.
 */
@Disabled
@TeleOp
public class TestBot  extends OpMode {
    DcMotor leftRear;
    DcMotor leftFront;
    DcMotor rightRear;
    DcMotor rightFront;


    @Override
    public void init() {
        leftFront = hardwareMap.dcMotor.get("motorLeftF");
        leftRear = hardwareMap.dcMotor.get("motorLeftR");
        rightRear = hardwareMap.dcMotor.get("motorRightF");
        rightFront = hardwareMap.dcMotor.get("motorRightR");
    }

    @Override
    public void loop() {
        leftFront.setPower(gamepad1.left_stick_y);
        leftRear.setPower(gamepad1.left_stick_y);

        rightFront.setPower(gamepad1.right_stick_y);
        rightRear.setPower(gamepad1.right_stick_y);
    }
}
