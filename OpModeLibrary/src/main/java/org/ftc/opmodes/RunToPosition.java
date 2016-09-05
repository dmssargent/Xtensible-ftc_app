package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by David on 8/11/2016.
 */
@TeleOp
public class RunToPosition extends OpMode {
    DcMotor test;
    private int target;

    @Override
    public void init() {
        test = hardwareMap.dcMotor.get("armLift");
        test.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        test.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void loop() {
        if (gamepad1.x) {
            test.setPowerFloat();
            target = test.getCurrentPosition();
        } else {
            //test.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            test.setTargetPosition(target);
            test.setPower(0.2);
        }
    }
}
