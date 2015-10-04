package org.ftc.opmodes;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.LinkedList;

public class BasicLayout extends ExtensibleOpMode {
    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) {

    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    /**
     * Something went wrong? This is where you process it.
     *
     * @param ctx the Robot context
     * @param eventType the type of event
     * @param event the actual event
     * @param in a failure event
     * @return an integer value representing what to do; values < 0 stops the robot
     */
    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }
}
