package org.firstinspires.ftc.teamcode.test;

import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

@TeleOp
public class TestOpMode extends TestOpModeParent {
    @Override
    public void init() {
        telemetry.addData("STATUS", "INIT");
        telemetry.addData("GAMEPAD1", gamepad1);
        telemetry.addData("GAMEPAD2", gamepad2);
        telemetry.addData("HARDWARE_MAP", hardwareMap);
    }

    @Override
    public void stop() {
        telemetry.addData("STATUS", "STOP");
        telemetry.addData("GAMEPAD1", gamepad1);
        telemetry.addData("GAMEPAD2", gamepad2);
        telemetry.addData("HARDWARE_MAP", hardwareMap);
    }

    @Override
    public void loop() {
        telemetry.addData("STATUS", "LOOP");
        telemetry.addData("GAMEPAD1", gamepad1);
        telemetry.addData("GAMEPAD2", gamepad2);
        telemetry.addData("HARDWARE_MAP", hardwareMap);
    }
}
