package org.ftc.opmodes;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import org.ftccommunity.ftcxtensible.dagger.annonations.Inject;
import org.ftccommunity.ftcxtensible.dagger.annonations.Named;
import org.ftccommunity.ftcxtensible.interfaces.RobotInitStopLoop;


public abstract class TestOpModeParent implements RobotInitStopLoop {
    @Inject
    protected Telemetry telemetry;
    @Inject
    protected Gamepad gamepad1;
    @Inject
    @Named("gamepad2")
    protected Gamepad gamepad2;
    @Inject
    protected HardwareMap hardwareMap;
}
