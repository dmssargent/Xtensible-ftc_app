package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp
@Disabled
public class RunToPosition extends OpMode {
    DcMotor test;
    private int target;

    @Override
    public void init() {
        test = hardwareMap.dcMotor.get("armLift");
        test.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        test.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void loop() {
        if (gamepad1.x) {
            test.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            target = test.getCurrentPosition();
        } else {
            test.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            test.setTargetPosition(target);
            test.setPower(0.2);
        }
    }
}
