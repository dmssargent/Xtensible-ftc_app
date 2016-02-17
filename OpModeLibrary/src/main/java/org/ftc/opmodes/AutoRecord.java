package org.ftc.opmodes;

import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

import java.util.LinkedList;

/**
 * Created by first on 2/11/16.
 */
@Autonomous
public class AutoRecord extends XAuto {
    @Override
    public void init(RobotContext ctx) throws Exception {
        gamepad1.startRecording(GAMEPAD_RECORD_NAME);
        robotInit();
        configureAutoDevices();
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> objects) throws Exception {
        gamepad1.stopRecording();
        robotStop(ctx, objects);
    }
}
