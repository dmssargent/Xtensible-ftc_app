package org.ftccommunity.ftcxtensible.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.ftccommunity.ftcxtensible.math.CartesianCoordinates;
import org.ftccommunity.ftcxtensible.math.PolarCoordinates;

import static com.google.common.base.Preconditions.checkArgument;

public class DriveTrain {
    private final DcMotor leftFront;
    private final DcMotor rightFront;
    private final DcMotor leftRear;
    private final DcMotor rightRear;
    private final double disableMap[];
    //private final DriveTrainTarget speedTarget;
    private final ExtensibleGamepad controlGamepad;
    //private GamepadDrive drive;
    private DriveSystem driveSystem;

    private double theta;
    private double leftMotorReading;
    private double rightMotorReading;


    public DriveTrain(ExtensibleGamepad controlGamepad, DcMotor leftFront, DcMotor rightFront, DcMotor leftRear, DcMotor rightRear) {
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
        //this.drive = drive;

        disableMap = new double[4];

        for (int i = 0; i < disableMap.length; i++) {
            disableMap[i] = 1;
        }
    }

    public DriveTrain(ExtensibleGamepad controlGamepad, RobotContext context, String leftFront, String rightFront, String leftRear, String rightRear) {
        this(controlGamepad,
                context.hardwareMap().dcMotor(leftFront), context.hardwareMap().dcMotor(rightFront),
                context.hardwareMap().dcMotor(leftRear), context.hardwareMap().dcMotor(rightRear));
    }

    public DriveTrain(Gamepad controlGamepad, HardwareMap hardwareMap, String leftFront, String rightFront, String leftRear, String rightRear) {
        this(null,
                hardwareMap.dcMotor.get(leftFront), hardwareMap.dcMotor.get(rightFront),
                hardwareMap.dcMotor.get(leftRear), hardwareMap.dcMotor.get(rightRear));
    }

    public void configureDriveSystem(DriveSystem drive) {
        if (drive instanceof DriveTrain.DriveSystems) {
            ((DriveSystems) drive).hwInterface = this;
        }

        driveSystem = drive;
    }

    void updateTargetWithGamepad() {
        updateTarget(controlGamepad.rightJoystick.X(), -controlGamepad.rightJoystick.Y(),
                controlGamepad.leftJoystick.X());
    }

    public void updateTarget(double x, double y, double rotPower) {
        driveSystem.drive(x, y, rotPower);
    }


    /**
     * @param index zero based index to disable
     */
    public void enableMotor(int index, boolean enabled) {
        disableMap[index] = enabled ? 1d : 0d;
    }

    public void tuneMotorInstance(int index, double value) {
        checkArgument(value >= 0 && value <= 1, "Out of range value");
        disableMap[index] = value;
    }

    public double motorTuningParameter(int index) {
        return disableMap[index];
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

    public enum DriveSystems implements DriveSystem {
        MECANUM {
            public void drive(double x, double y, double rotPower) {
                PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(x, y));

                hwInterface.theta = coordinates.getTheta();
                final double robotAngle = hwInterface.theta - Math.PI / 4;

                final double r = coordinates.getR();
                double v1 = r * Math.cos(robotAngle) + rotPower;
                double v2 = r * Math.sin(robotAngle) - rotPower;
                double v3 = r * Math.sin(robotAngle) + rotPower;
                double v4 = r * Math.cos(robotAngle) - rotPower;
//        if (theta < Math.PI / 4 && theta > -Math.PI / 4) {
//            v2 *= .55;
//            v3 *= .65;
//            v4 *= .45;
//        } else if (theta < 3 * Math.PI / 4 && theta > -3 * Math.PI / 4) {
//            v2 *= .55;
//            v3 *= .65;
//            v4 *= .45;
//        }
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

                v1 *= hwInterface.disableMap[0];
                v2 *= hwInterface.disableMap[1];
                v3 *= hwInterface.disableMap[2];
                v4 *= hwInterface.disableMap[3];

                hwInterface.leftFront.setPower(v1);
                hwInterface.rightFront.setPower(v2);
                hwInterface.leftRear.setPower(v3);
                hwInterface.rightRear.setPower(v4);
            }
        }, HOLONOMIC {
            @Override
            public void drive(double x, double y, double rotPower) {
                //double transLength = Math.hypot(x, y);
                double adjY = y;
                double adjX = x;
                double frontLeft = adjY + adjX + rotPower;
                double frontRight = adjY - adjX - rotPower;
                double backRight = adjY + adjX - rotPower;
                double backLeft = adjY - adjX + rotPower;

                frontLeft = Range.clip(frontLeft, -1, 1);
                frontRight = Range.clip(frontRight, -1, 1);
                backLeft = Range.clip(backLeft, -1, 1);
                backRight = Range.clip(backRight, -1, 1);

                frontLeft *= hwInterface.disableMap[0];
                frontRight *= hwInterface.disableMap[1];
                backLeft *= hwInterface.disableMap[2];
                backRight *= hwInterface.disableMap[3];

                hwInterface.leftFront.setPower(frontLeft);
                hwInterface.rightFront.setPower(frontRight);
                hwInterface.leftRear.setPower(backLeft);
                hwInterface.rightRear.setPower(backRight);

            }
        };

        public DriveTrain hwInterface;
    }

    public interface DriveSystem {
        void drive(double x, double y, double rotPower);
    }
//
//    enum DriveSystems implements GamepadDrive {
//        Mecanum {
//            @Override
//            public void drive(double leftX, double leftY, double rightX, double rightY, DriveTrainTarget target) {
//                PolarCoordinates coordinates = new PolarCoordinates(new CartesianCoordinates(leftX, leftY));
//
//                final double robotAngle = coordinates.getTheta() - Math.PI / 4;
//
//                //final double r = coordinates.getR();
//                double r = coordinates.getR();
//                final double v1 = .54 * r * Math.cos(robotAngle) + rightX;
//                final double v2 = .54 * r * Math.sin(robotAngle) - rightX;
//                final double v3 = r * Math.sin(robotAngle) + rightX;
//                final double v4 = r * Math.cos(robotAngle) - rightX;
//                /* Basic depicition of layout ("/" and "\" represents the tread direction of the wheel)
//
//                   \ 1 2 /
//                   / 3 4 \
//
//                 */
////                final double robotAngle = coordinates.getTheta() + Math.PI / 4;
////                final double r = coordinates.getR();
////                double v1 = r * Math.sin(robotAngle) + rightY;
////                double v2 = r * Math.cos(robotAngle) - rightY;
////                double v3 = r * Math.cos(robotAngle) + rightY;
////                double v4 = r * Math.sin(robotAngle) - rightY;
////                v1 = Range.clip(v1, -1, 1);
////                v2 = Range.clip(v2, -1, 1);
////                v3 = Range.clip(v3, -1, 1);
////                v4 = Range.clip(v4, -1, 1);
//
//                target.leftFront = v1;
//                target.rightFront = v2;
//                target.leftRear = v3;
//                target.rightRear = v4;
//            }
//        }
//    }
//
//
//    interface GamepadDrive {
//        void drive(double leftX, double leftY, double rightX, double rightY, DriveTrainTarget target);
//    }
//
//    private class DriveTrainTarget {
//        private double leftFront;
//        private double rightFront;
//        private double leftRear;
//        private double rightRear;
//    }
}
