package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.robot.RobotContext;


class ClutchHardware {

    private static final double SERVO_POSITION_LEFT = .25;
    private static final double SERVO_POSITION_RIGHT = .75;
    private static final double SERVO_POSITION_NEUTRAL = .5;
    final DriveTrain driveTrain;
    final OpticalDistanceSensor opticalDistanceSensor;
    final UltrasonicSensor distanceSensor;
    final AdafruitSensorWrapper colorSensor;
    private final Servo colorServo;

    ClutchHardware(RobotContext ctx) {
        // Set up drive train
        driveTrain = new DriveTrain(ctx.gamepad1(), null, ctx, "leftFront", "rightFront", "leftRear", "rightRear");
        // Obtain sensors
        final ExtensibleHardwareMap hardwareMap = ctx.hardwareMap();
        opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        distanceSensor = hardwareMap.ultrasonicSensors().get("ultrasonic");
        ((ModernRoboticsI2cRangeSensor) distanceSensor).read8(ModernRoboticsI2cRangeSensor.Register.FIRMWARE_REV);
        colorServo = hardwareMap.servos().get("colorServo");
        colorSensor = new AdafruitSensorWrapper(hardwareMap.colorSensors().get("colorSensor"),
                hardwareMap.deviceInterfaceModules().get("dim"), 1, false);
    }

    final void pressLeftBeacon() {
        colorServo.setPosition(SERVO_POSITION_LEFT);
    }

    final void pressRightBeacon() {
        colorServo.setPosition(SERVO_POSITION_RIGHT);
    }

    void pressNietherBeaconButton() {
        colorServo.setPosition(SERVO_POSITION_NEUTRAL);
    }
}
