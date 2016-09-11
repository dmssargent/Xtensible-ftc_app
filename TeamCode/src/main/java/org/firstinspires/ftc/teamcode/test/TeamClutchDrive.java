package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.interfaces.RobotAction;
import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;
import org.ftccommunity.ftcxtensible.robot.Async;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

@TeleOp
@Disabled
public class TeamClutchDrive extends SimpleOpMode {
    protected DcMotor right0;
    protected DcMotor right1;
    protected DcMotor left0;
    protected DcMotor left1;

    protected DcMotor armLift;
    protected DcMotor armWinch;
    protected double ARM_MOTOR_BASE = 0.50;
    protected DcMotor armPivot;
    protected Servo climbingMenHolder;
    protected double climbingMenHolderPosition = .75;
    private int stall;
    private int loopCount;
    private UltrasonicSensor ultrasonic;
    //private boolean ledEnabled = false;

    private AdafruitSensorWrapper color;

    private DeviceInterfaceModule dim;
    private int armPosition = 0;
    private boolean lastChangeLed;

    protected void robotInit() {
        final ColorSensor colorSensor = hardwareMap.colorSensors().get("colorSensor");
        dim = hardwareMap.deviceInterfaceModules().get("Device Interface Module 1");
        color = new AdafruitSensorWrapper(colorSensor, dim, 0, false);
        /// Configure color sensor led

        // Controller S0: "armMotorCtrl" encoded
        armLift = hardwareMap.dcMotors().get("armLift");
        armPivot = hardwareMap.dcMotors().get("armPivot");
        /// Setup encoder
        armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armPosition = armLift.getCurrentPosition();

        // Controller S1 "rghtDriveCtrl"
        right0 = hardwareMap.dcMotors().get("right0");
        right1 = hardwareMap.dcMotors().get("right1");
        /// Configure right drive motors
        right0.setDirection(DcMotor.Direction.REVERSE);
        right1.setDirection(DcMotor.Direction.REVERSE);
        // Controller S2: "servos"
        climbingMenHolder = hardwareMap.servos().get("menHolder");
        // Controller S3: "armWinch"
        armWinch = hardwareMap.dcMotors().get("armWinch");
        // Ultrasonic S4: "ultrasonic"
        ultrasonic = hardwareMap.ultrasonicSensors().get("ultrasonic");
        // Controller S5: "leftDriveCtrl"
        left0 = hardwareMap.dcMotors().get("left0");
        left1 = hardwareMap.dcMotors().get("left1");

        gamepad1.when(ExtensibleGamepad.Buttons.A, true, new RobotAction() {
            @Override
            public void perform() {
                climbingMenHolderPosition = .25;
            }
        }).when(ExtensibleGamepad.Buttons.B, true, new RobotAction() {
            @Override
            public void perform() {
                climbingMenHolderPosition = .65;
            }
        }).when(ExtensibleGamepad.Triggers.RIGHT_TRIGGER, com.google.common.collect.Range.closed(0.5, 1d), new RobotAction() {
            @Override
            public void perform() {
                if (!lastChangeLed) {
                    color.enableLed(!color.isLedEnabled());
                    color.beginColorSample(50);

                    if (color.isSampleDone()) {
                        color.defineColorAsNeutral(color.averageRed(), color.averageGreen(), color.averageBlue());
                    }
                }
                lastChangeLed = true;
            }
        });
    }

    protected void robotDrive() {
        double leftSpeed = gamepad1.leftJoystick.Y();
        double rightSpeed = gamepad1.rightJoystick.Y();
        if (!gamepad1.isRightBumperPressed()) {
            leftSpeed = scaleDriveMotorSpeed(leftSpeed);
            rightSpeed = scaleDriveMotorSpeed(rightSpeed);
        }

        left0.setPower(leftSpeed);
        left1.setPower(leftSpeed);
        right0.setPower(rightSpeed);
        right1.setPower(rightSpeed);

        double wrenchPower = gamepad1.dpad.isUpPressed() ? ARM_MOTOR_BASE : 0;
        wrenchPower = gamepad1.dpad.isDownPressed() ? -ARM_MOTOR_BASE : wrenchPower;
        armWinch.setPower(wrenchPower);

        climbingMenHolder.setPosition(climbingMenHolderPosition);

        double armPower = gamepad1.dpad.isRightPressed() ? ARM_MOTOR_BASE : 0;
        armPower = gamepad1.dpad.isLeftPressed() ? -ARM_MOTOR_BASE : armPower;
        armPosition += gamepad1.dpad.isRightPressed() ? 1 : 0;
        armPosition -= gamepad1.dpad.isLeftPressed() ? 1 : 0;


//        armLift.setTargetPosition(armPosition);
//        final int currentPosition = armLift.getCurrentPosition();
//        if (!isCloseTo(armPosition, currentPosition, 10)) {
//            if (currentPosition < armPosition) {
//                armWinch.setPower(ARM_MOTOR_BASE);
//            } else if (currentPosition > armPosition) {
//                armWinch.setPower(-ARM_MOTOR_BASE);
//            }
//            armLift.setPower(0.25);
//            stall++;
//        } else {
//            if (!isCloseTo(armPosition, currentPosition, 3)) {
//                armLift.setPower(0.25);
//                stall++;
//            } else {
//                armLift.setPower(armPower);
//            }
//        }
//
//        if (stall >= 5) {
//            armLift.setTargetPosition(currentPosition);
//            stall = 0;
//        }
//        if (gamepad1.left_trigger > .5) {
//            armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            armLift.setPower(0);
//        } else {
//            armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        }

        if (gamepad1.isXPressed()) {
            armPivot.setPower(ARM_MOTOR_BASE / 2);
        } else if (gamepad1.isYPressed()) {
            armPivot.setPower(-ARM_MOTOR_BASE / 2);
        } else {
            armPivot.setPower(0);
        }

//        final boolean positionChanged;
//        if (gamepad1.isXPressed()) {
//            positionChanged = true;
//            climbingMenHolderPosition = .65;
//        } else if (gamepad1.isYPressed()) {
//            positionChanged = true;
//            climbingMenHolderPosition = .25;
//        } else {
//            positionChanged = false;
//        }
//
//        if (positionChanged) {
//            climbingMenHolder.setPosition(climbingMenHolderPosition);
//        }

//        final boolean changeLed = gamepad1.right_trigger > .5;
//        if (changeLed && !lastChangeLed) {
//            color.enableLed(!color.isLedEnabled());
//            color.beginColorSample(50);
//
//            if (color.isSampleDone()) {
//                color.defineColorAsNeutral(color.averageRed(), color.averageGreen(), color.averageBlue());
//            }
//        }
//        lastChangeLed = changeLed;

        //color.update();

        telemetry.data("Left", leftSpeed);
        telemetry.data("Right", rightSpeed);


//        switch (color.redOrBlue()) {
//            case RED:
//                dim.setLED(0, false);
//                dim.setLED(1, true);
//                break;
//            case BLUE:
//                dim.setLED(0, true);
//                dim.setLED(1, false);
//                break;
//            case GREEN:
//            case NONE:
//                dim.setLED(0, false);
//                dim.setLED(1, false);
//                break;
//        }


        //telemetry.addData("Red-Blue1:", color.red() > color.blue() ? "Red" : color.red() == color.blue() ? "Unknown" : "Blue");
        // telemetry.addData("Red-Blue2:", (color.rawRed() - color.getRedBlueWhiteDelta()) > color.rawBlue() ? "Red" : (color.rawRed() - color.getRedBlueWhiteDelta()) == color.rawBlue() ? "Unknown" : "Blue");
        telemetry.data("Ultrasonic", ultrasonic.getUltrasonicLevel());
        // telemetry.addData("Encoder", currentPosition);
        telemetry.data("Arm Power", ARM_MOTOR_BASE + " " + armPower + " " + wrenchPower);

    }

    private float scaleDriveMotorSpeed(double number) {
        return (float) Range.clip(1.68 * Math.pow(Math.tanh(number), 3), -1, 1);
    }

    private boolean isCloseTo(int x, int y, int amount) {
        return Math.abs(x - y) <= amount;
    }

    @Async
    public void sensorLoop() {
        long time = System.nanoTime();
        int loop = 0;
        RobotLog.i("Starting sensor loop");
        telemetry.data("SENSOR_LOOP_S", "starting");

        while (!isStopped()) {
            loop++;
            RobotLog.d("looping sensor loop");
            RobotLog.d("isInterrupted " + Thread.currentThread().isInterrupted());
            telemetry.data("SENSOR_LOOP_S", "updating");
            color.update();
            telemetry.data("SENSOR_LOOP_S", "led");
            switch (color.redOrBlue()) {
                case RED:
                    dim.setLED(0, false);
                    dim.setLED(1, true);
                    break;
                case BLUE:
                    dim.setLED(0, true);
                    dim.setLED(1, false);
                    break;
                case GREEN:
                case NONE:
                    dim.setLED(0, false);
                    dim.setLED(1, false);
                    break;
            }
            telemetry.data("SENSOR_LOOP", (System.nanoTime() - time) / (double) loop);
            telemetry.data("RGB_N", color.red() + " " + color.green() + " " + color.blue());
            telemetry.data("RGB_L", color.low(color.rawRed()) + " " + color.low(color.rawGreen()) + " " + color.low(color.rawBlue()));
            telemetry.data("Sampling", color.isSampling());
            telemetry.data("SENSOR_LOOP_S", "running");
        }
    }


    @Override
    public void init(RobotContext ctx) throws Exception {
        robotInit();
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        robotDrive();
    }

//    @Override
//    public void stop() {
//        robotStop(ctx, objects);
//    }
//
//    protected void robotStop() {
//        super.stop(ctx, objects);
//    }

//    protected boolean pivotInWriteMode() {
//        return true; //armPivot.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY;
//    }
}
