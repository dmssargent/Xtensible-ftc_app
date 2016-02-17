package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by First on 12/15/2015.
 */
@TeleOp
public class TestArm extends OpMode {
    private DcMotor armLift;

    private int loopCycle = 0;
    private int encoder = 0;

    private boolean inited;
    private boolean resetEncoders;

    @Override
    public void init() {
        armLift = hardwareMap.dcMotor.get("armLift");
        armLift.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        armLift.getController().setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        inited = false;
    }

    @Override
    public void loop() {
        if (armLift.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {
            encoder = armLift.getCurrentPosition();

            if (!inited) {
                if (armLift.getCurrentPosition() == 0) {
                    resetEncoders = true;
                }

                inited = true;
            } else {
                armLift.getController().setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
            }

        }

        if (armLift.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY) {
            if (resetEncoders) {
                armLift.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            }
            armLift.setPower(gamepad1.x ? .2 : .0);
            armLift.setPower(gamepad1.y ? -.2 : .0);
            loopCycle++;

            if (loopCycle > 20) {
                armLift.setPower(0);
                armLift.getController().setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
            }
        }

        telemetry.addData("ENCODER", encoder);
        telemetry.addData("MODE:", armLift.getController().getMotorControllerDeviceMode().toString());
    }
}
