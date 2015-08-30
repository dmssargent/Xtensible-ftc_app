package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.ftccommunity.networkedopmode.NetworkedOpMode;
import org.ftccommunity.networkedopmode.RobotContext;

public class TestOpMode extends OpMode {
    private NetworkedOpMode network;

    @Override
    public void init() {
        network = new NetworkedOpMode(new RobotContext(gamepad1, gamepad2, hardwareMap));
        network.startServer();
    }

    @Override
    public void loop() {
        int i = 1 + 1;
    }

    @Override
    public void stop() {
        super.stop();
        network.stopServer();
    }
}
