package org.ftc.opmodes;

import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

@TeleOp
public class ZTeamClutchTeleop extends TeamClutchDrive {
    private int postion;

    public void loop(RobotContext ctx) {
        if (pivotInReadMode()) {
            postion = armPivot.getCurrentPosition();
            armPivot.getController().setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        }

        telemetry.data("ARM Positon", postion);

        if (pivotInWriteMode() && getLoopCount() % 20 == 0) {
            armPivot.getController().setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        }
    }

    private boolean pivotInReadMode() {
        return armPivot.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY;
    }
}
