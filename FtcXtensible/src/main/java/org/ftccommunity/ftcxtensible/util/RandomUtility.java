package org.ftccommunity.ftcxtensible.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


public final class RandomUtility {
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

    boolean closeTo(double current, double wanted, double tolerance) {
        return Math.abs(current - wanted) < tolerance;
    }
}
