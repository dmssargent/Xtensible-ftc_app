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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.ftccommunity.ftcxtensible.opmodes.RobotsDontQuit;
import org.ftccommunity.ftcxtensible.robot.Async;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.robot.RobotLogger;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.Date;
import java.util.LinkedList;

@Autonomous(name = "Hello World Example")
@RobotsDontQuit
public class HelloWorld extends SimpleOpMode {
    private final String MESS = "MESS";
    private volatile boolean test = false;

    /**
     * Changes the target position of a {@link DcMotor} by if a button on a {@link com.qualcomm.robotcore.hardware.Gamepad}
     * has been pressed. The button is represented by a {@code boolean} value: {@code true} if the
     * button is currently pressed, and {@code false} if the button is not currently pressed.
     *
     * @param button            the current button state
     *                          (example: {@link com.qualcomm.robotcore.hardware.Gamepad#a}
     * @param lastButtonState   the last state that the button was in.
     *                          In other words, the value of the same button as it was in the last
     *                          iteration of the {@link OpMode#loop()}
     * @param dcMotor           the {@code DcMotor} to adjust the target parameter. This doesn't configure
     *                          the motor for use of a target position. This just the pre-existing target
     *                          position of the motor
     * @param incrementByAmount by how much should the target position be incremented by
     * @return the current button value. This allows for:
     * <code>lastTime = changeMotorTargetOnButtonPress(gamepad1.a, lastTime, motor, 720);</code>
     */
    static boolean changeMotorTargetOnButtonPress(boolean button, boolean lastButtonState, final DcMotor dcMotor, int incrementByAmount) {
        if (dcMotor == null) throw new NullPointerException("dcMotor can't be null");
        if (!lastButtonState && button) {
            dcMotor.setTargetPosition(dcMotor.getTargetPosition() + incrementByAmount);
        }
        return button;


    }

    /**
     * Changes the target position of a {@link DcMotor} by if a button on a {@link com.qualcomm.robotcore.hardware.Gamepad}
     * has been released. The button is represented by a {@code boolean} value: {@code true} if the
     * button is currently pressed, and {@code false} if the button is not currently pressed.
     *
     * @param button            the current button state
     *                          (example: {@link com.qualcomm.robotcore.hardware.Gamepad#a}
     * @param lastButtonState   the last state that the button was in.
     *                          In other words, the value of the same button as it was in the last
     *                          iteration of the {@link OpMode#loop()}
     * @param dcMotor           the {@code DcMotor} to adjust the target parameter. This doesn't configure
     *                          the motor for use of a target position. This just the pre-existing target
     *                          position of the motor
     * @param incrementByAmount by how much should the target position be incremented by
     * @return the current button value. This allows for:
     * <code>lastTime = changeMotorTargetOnButtonPress(gamepad1.a, lastTime, motor, 720);</code>
     */
    static boolean changeMotorTargetOnButtonRelease(boolean button, boolean lastButtonState, final DcMotor dcMotor, int incrementByAmount) {
        if (dcMotor == null) throw new NullPointerException("dcMotor can't be null");
        if (lastButtonState && !button) {
            dcMotor.setTargetPosition(dcMotor.getTargetPosition() + incrementByAmount);
        }
        return button;
    }

    @Override
    public void init(RobotContext ctx) {
        enableNetworking().startNetworking();
    }

    @Override
    public void loop(RobotContext ctx) {
        //RobotLogger.i(MESS, "Current loop count: " + String.valueOf(getLoopCount()));
        telemetry.data(MESS, "Hello, World!");
        telemetry.data(MESS, "How are you doing?");
        telemetry.data("TEST", test);

    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {
        telemetry.data(MESS, "Start Date: " +
                (new Date((long) (System.nanoTime() / 1E3))).toString());
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        RobotLogger.w(MESS, "End Date: " +
                (new Date(System.nanoTime() / 1000)).toString() + ". This ran for " + getRuntime());
    }

    @Async
    public void testFunc() {
        while (!Thread.currentThread().isInterrupted()) {
            test = !test;
            telemetry.data("TEST2", "Hi!");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    boolean closeTo(double current, double wanted, double tolerance) {
        return Math.abs(current - wanted) < tolerance;
    }



}
