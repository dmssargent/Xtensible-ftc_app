package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.robot.RobotContext;


class ClutchHardware {

    private static final double SERVO_POSITION_LEFT = .40;
    private static final double SERVO_POSITION_RIGHT = .60;
    private static final double SERVO_POSITION_NEUTRAL = .5;
    final DriveTrain driveTrain;
    final OpticalDistanceSensor opticalDistanceSensor;
    final ModernRoboticsI2cRangeSensor distanceSensor;
    final ColorSensor colorSensor;
    final DcMotor winch;
    final Servo pressed;
    final Servo ballLift;
    private final DeviceInterfaceModule dim;
    //private final Servo colorServo;

    ClutchHardware(RobotContext ctx) {
        // Set up drive train
        driveTrain = new DriveTrain(ctx.gamepad1(), ctx, "leftFront", "rightFront", "leftRear", "rightRear");
        // Obtain sensors
        final ExtensibleHardwareMap hardwareMap = ctx.hardwareMap();
        opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        distanceSensor = ctx.legacyHardwareMap().get(ModernRoboticsI2cRangeSensor.class, "ultrasonic");
        //colorServo = hardwareMap.servos().get("colorServo");
        colorSensor = hardwareMap.colorSensors().get("colorSensor");
        dim = hardwareMap.deviceInterfaceModules().get("dim");
        winch = hardwareMap.get("winch");
        pressed = hardwareMap.get("buttonPresser");
        ballLift = hardwareMap.get("ballLift");
    }

    ClutchHardware(Gamepad gamepad1, HardwareMap map) {
        // Set up drive train
        driveTrain = new DriveTrain(gamepad1, map, "leftFront", "rightFront", "leftRear", "rightRear");
        // Obtain sensors
        final HardwareMap hardwareMap = map;
        opticalDistanceSensor = hardwareMap.opticalDistanceSensor.get("opticalDistance");
        distanceSensor = map.get(ModernRoboticsI2cRangeSensor.class, "ultrasonic");
        //colorServo = hardwareMap.servos().get("colorServo");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        dim = hardwareMap.deviceInterfaceModule.get("dim");
        winch = (DcMotor) hardwareMap.get("winch");
        pressed = (Servo) hardwareMap.get("buttonPresser");
        ballLift = (Servo) hardwareMap.get("ballLift");
    }

    final void pressLeftBeacon() {
        pressed.setPosition(SERVO_POSITION_LEFT);
    }

    final void pressRightBeacon() {
        pressed.setPosition(SERVO_POSITION_RIGHT);
    }

    void pressNietherBeaconButton() {
        pressed.setPosition(SERVO_POSITION_NEUTRAL);
    }
}
