package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
import org.ftccommunity.ftcxtensible.math.PolarCoordinates;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.RobotContext;

public class DriveTrain {
    private final DcMotor leftFront;
    private final DcMotor rightFront;
    private final DcMotor leftRear;
    private final DcMotor rightRear;
    private final int disableMap[];
    private final DriveTrainTarget speedTarget;
    private final ExtensibleGamepad controlGamepad;
    private GamepadDrive drive;

    private double theta;
    private double leftMotorReading;
    private double rightMotorReading;


    public DriveTrain(ExtensibleGamepad controlGamepad, GamepadDrive drive, DcMotor leftFront, DcMotor rightFront, DcMotor leftRear, DcMotor rightRear) {
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.leftRear = leftRear;
        this.rightRear = rightRear;
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRear.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.controlGamepad = controlGamepad;
        this.drive = drive;
        speedTarget = new DriveTrainTarget();
        disableMap = new int[4];

        for (int i = 0; i < disableMap.length; i++) {
            disableMap[i] = 1;
        }
    }

    public DriveTrain(ExtensibleGamepad controlGamepad, GamepadDrive drive, RobotContext context, String leftFront, String rightFront, String leftRear, String rightRear) {
        this(controlGamepad, drive,
                context.hardwareMap().dcMotor(leftFront), context.hardwareMap().dcMotor(rightFront),
                context.hardwareMap().dcMotor(leftRear), context.hardwareMap().dcMotor(rightRear));
    }

    public DriveTrain(Gamepad controlGamepad, GamepadDrive drive, HardwareMap hardwareMap, String leftFront, String rightFront, String leftRear, String rightRear) {
        this(null, drive,
                hardwareMap.dcMotor.get(leftFront), hardwareMap.dcMotor.get(rightFront),
                hardwareMap.dcMotor.get(leftRear), hardwareMap.dcMotor.get(rightRear));
    }

    void updateTargetWithGamepad() {
        updateTarget(controlGamepad.rightJoystick.X(), -controlGamepad.rightJoystick.Y(),
                controlGamepad.leftJoystick.X());
    }

    void updateTarget(double x, double y, double rotPower) {
        PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(x, y));

        theta = coordinates.getTheta();
        final double robotAngle = theta - Math.PI / 4;

        final double r = coordinates.getR();
        double v1 = r * Math.cos(robotAngle) + rotPower;
        double v2 = r * Math.sin(robotAngle) - rotPower;
        double v3 = r * Math.sin(robotAngle) + rotPower;
        double v4 = r * Math.cos(robotAngle) - rotPower;
        if (theta < Math.PI / 4 && theta > -Math.PI / 4) {
            v2 *= .55;
            v3 *= .65;
            v4 *= .45;
        } else if (theta < 3 * Math.PI / 4 && theta > -3 * Math.PI / 4) {
            v2 *= .55;
            v3 *= .65;
            v4 *= .45;
        }
                /* Basic depicition of layout ("/" and "\" represents the tread direction of the wheel)

                   \ 1 2 /
                   / 3 4 \

                 */
//                final double robotAngle = coordinates.getTheta() + Math.PI / 4;
//                final double r = coordinates.getR();
//                double v1 = r * Math.sin(robotAngle) + rightY;
//                double v2 = r * Math.cos(robotAngle) - rightY;
//                double v3 = r * Math.cos(robotAngle) + rightY;
//                double v4 = r * Math.sin(robotAngle) - rightY;
//                v1 = Range.clip(v1, -1, 1);
//                v2 = Range.clip(v2, -1, 1);
//                v3 = Range.clip(v3, -1, 1);
//                v4 = Range.clip(v4, -1, 1);

        v1 *= disableMap[0];
        v2 *= disableMap[1];
        v3 *= disableMap[2];
        v4 *= disableMap[3];

        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftRear.setPower(v3);
        rightRear.setPower(v4);

        leftMotorReading = leftFront.getCurrentPosition();
        rightMotorReading = rightFront.getCurrentPosition();
    }

    void flushTargetToMotors() {
        leftFront.setPower(speedTarget.leftFront);
        leftRear.setPower(speedTarget.leftRear);
        rightFront.setPower(speedTarget.rightFront);
        rightRear.setPower(speedTarget.rightRear);
    }

    public void updateUsingGamepadAndFlush() {
        flushTargetToMotors();
    }

    /**
     * @param index zero based index to disable
     */
    public void enableMotor(int index, boolean enabled) {
        disableMap[index] = enabled ? 1 : 0;
    }

    public double getLeftMotorReading() {
        return leftMotorReading;
    }

    public double getRightMotorReading() {
        return rightMotorReading;
    }

    public double getThetaInDegrees() {
        return theta / Math.PI * 180;
    }

    enum DriveSystems implements GamepadDrive {
        Mecanum {
            @Override
            public void drive(double leftX, double leftY, double rightX, double rightY, DriveTrainTarget target) {
                PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(leftX, leftY));

                final double robotAngle = coordinates.getTheta() - Math.PI / 4;

                //final double r = coordinates.getR();
                double r = coordinates.getR();
                final double v1 = .54 * r * Math.cos(robotAngle) + rightX;
                final double v2 = .54 * r * Math.sin(robotAngle) - rightX;
                final double v3 = r * Math.sin(robotAngle) + rightX;
                final double v4 = r * Math.cos(robotAngle) - rightX;
                /* Basic depicition of layout ("/" and "\" represents the tread direction of the wheel)

                   \ 1 2 /
                   / 3 4 \

                 */
//                final double robotAngle = coordinates.getTheta() + Math.PI / 4;
//                final double r = coordinates.getR();
//                double v1 = r * Math.sin(robotAngle) + rightY;
//                double v2 = r * Math.cos(robotAngle) - rightY;
//                double v3 = r * Math.cos(robotAngle) + rightY;
//                double v4 = r * Math.sin(robotAngle) - rightY;
//                v1 = Range.clip(v1, -1, 1);
//                v2 = Range.clip(v2, -1, 1);
//                v3 = Range.clip(v3, -1, 1);
//                v4 = Range.clip(v4, -1, 1);

                target.leftFront = v1;
                target.rightFront = v2;
                target.leftRear = v3;
                target.rightRear = v4;
            }
        }
    }


    interface GamepadDrive {
        void drive(double leftX, double leftY, double rightX, double rightY, DriveTrainTarget target);
    }

    private class DriveTrainTarget {
        private double leftFront;
        private double rightFront;
        private double leftRear;
        private double rightRear;
    }
}
