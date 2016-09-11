/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.interfaces.JoystickScaler;
import org.ftccommunity.ftcxtensible.math.PolarCoordinates;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkApiLevel;
import org.ftccommunity.ftcxtensible.versioning.RobotSdkVersion;

import java.util.LinkedList;

/**
 * An arcade drive example, note that it is not finshed
 */
@TeleOp
@RobotSdkVersion(RobotSdkApiLevel.R3_2015)
@Disabled
public class ArcadeDriveTeleop extends ExtensibleOpMode {
    double lastTurnFactor;
    private DcMotor driveForwardLeft;
    private DcMotor driveForwardRight;
    private DcMotor driveRearLeft;
    private DcMotor driveRearRight;

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        driveForwardLeft = ctx.hardwareMap().dcMotors().get("red");
        driveForwardRight = ctx.hardwareMap().dcMotors().get("blue");
        driveRearLeft = ctx.hardwareMap().dcMotors().get("green");
        driveRearRight = ctx.hardwareMap().dcMotors().get("yellow");

        ctx.gamepad1().setupJoystickScalers(new JoystickScaler() {
            @Override
            public double scaleX(ExtensibleGamepad gamepad, double x) {
                return x;
            }

            @Override
            public double scaleY(ExtensibleGamepad gamepad, double y) {
                //return Range.clip(2.2842 * Math.pow(Math.tanh(y), 3d), -1, 1);
                return y;
            }

            @Override
            public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }

            @Override
            public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }
        }, new JoystickScaler() {
            @Override
            public double scaleX(ExtensibleGamepad gamepad, double x) {
                return 0;
            }

            @Override
            public double scaleY(ExtensibleGamepad gamepad, double y) {
                return 0;
            }

            @Override
            public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }

            @Override
            public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
                return 0;
            }
        });
    }

    @Override
    public void init_loop(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        PolarCoordinates joystick = gamepad1().rightJoystick().polar().invert();
        double r = joystick.getR();
        r = Range.clip(2.2842 * Math.pow(Math.tanh(r), 3d), -1, 1);
        joystick = new PolarCoordinates(r, joystick.getTheta());

        // TODO: finish this
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        driveForwardLeft.setPower(0);
        driveForwardRight.setPower(0);
        driveRearRight.setPower(0);
        driveRearRight.setPower(0);
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, RobotStatus.Type eventType, Object event, Object in) {
        return -1;
    }
}
