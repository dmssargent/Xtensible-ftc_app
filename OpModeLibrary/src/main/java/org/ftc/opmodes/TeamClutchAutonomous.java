package org.ftc.opmodes;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.util.LinkedList;

/**
 * Created by first on 2/11/16.
 */
@Autonomous
public class TeamClutchAutonomous extends XAuto {
    @Override
    public void init(RobotContext ctx) throws Exception {

        gamepad1.startPlayback(GAMEPAD_RECORD_NAME);
        robotInit();
        configureAutoDevices();
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        autoTelemetry();
        if (ultrasonicSensor.getUltrasonicLevel() > 10) {
            gamepad1.resumePlayback();
            super.loop(ctx);
        } else {
            gamepad1.pausePlayback();
            stopRobot();
        }
    }


    @Override
    public void stop(RobotContext ctx, LinkedList<Object> objects) throws Exception {
        if (gamepad1.isPlayingBack()) {
            gamepad1.stopPlayback();
        }
        robotStop(ctx, objects);
    }
}
