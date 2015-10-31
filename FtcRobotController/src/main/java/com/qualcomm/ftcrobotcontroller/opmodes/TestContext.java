package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;

/**
 * Created by David on 9/15/2015.
 */
@Autonomous
public class TestContext extends OpMode {
    boolean wasTrue;

    @Override
    public void init() {
        if (hardwareMap.appContext != null) {
            wasTrue = true;
        }
    }

    @Override
    public void loop() {
        if (hardwareMap.appContext == null) {
            telemetry.addData("CONTEXT", "Context is null");
        }
        telemetry.addData("INIT", wasTrue);
    }
}
