package org.ftc.opmodes;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.xtensible.xsimplify.SimpleOpMode;


public class SimpleDemo extends SimpleOpMode {
    // These variables should be automatically assigned a value upon execution, assuming you
    // named them the same as in the config file
    public DcMotor left;
    public DcMotor right;

    /**
     * Strange constructor, working on a better way
     */
    public SimpleDemo() {
        super(new SimpleDemo());
    }

    @Override
    public void init(RobotContext ctx) {

    }

    @Override
    public void loop(RobotContext ctx) {

    }
}
