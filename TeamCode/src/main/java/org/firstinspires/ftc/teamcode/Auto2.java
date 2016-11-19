package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.clutchauto.nav.Navigation;
import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.interfaces.RobotAction;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.EnumSet;

/**
 * Created by mhsrobotics on 11/19/16.
 */

public class Auto2 extends SimpleOpMode {
    private Navigation navigation;
    private DriveTrain driveTrain;
    private OpticalDistanceSensor opticalDistanceSensor;
    private UltrasonicSensor distanceSensor;
    private EnumSet<RobotStates> states;
    private AdafruitSensorWrapper colorSensor;
    private Servo colorServo;

    @Override
    public void init(RobotContext ctx) throws Exception {
        navigation = new Navigation(ctx.appContext());
//        leftFront = hardwareMap.dcMotor("leftFront");
//        leftRear = hardwareMap.dcMotor("leftRear");
//        rightFront = hardwareMap.dcMotor("rightFront");
//        rightRear = hardwareMap.dcMotor("rightRear");
        driveTrain = new DriveTrain(gamepad1, null, this, "leftFront", "rightFront", "leftRear", "rightRear");
        try {
            opticalDistanceSensor = hardwareMap.opticalDistanceSensors().get("opticalDistance");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Optical Distance sensor; \"opticalDistance\"");
        }
        try {
            distanceSensor = hardwareMap.ultrasonicSensors().get("ultrasonic");
        } catch (IllegalArgumentException ex) {
            RobotLog.i("Can't obtain Ultrasonic sensor; \"ultrasonic\"");
        }

        states = EnumSet.allOf(RobotStates.class);

    }

    @Override
    public void loop(RobotContext ctx) throws Exception {

    }

    enum RobotStates implements RobotAction {
        START_DRIVING {
            private ElapsedTime time;

            @Override
            public void perform() {
                if (opMode.opticalDistanceSensor.getRawLightDetected() > 200) {
                    nextState = LINE_FOLLOWING;
                } else {
                    nextState = this;
                }

                if (time.seconds() > 3)
                    opMode.driveTrain.updateTarget(0, .1, 0);
                else if (time.seconds() > 2.5)
                    opMode.driveTrain.updateTarget(0, .2, 0);
                else if (time.seconds() > 2)
                    opMode.driveTrain.updateTarget(0, .5, 0);
                else if (time.seconds() > 1)
                    opMode.driveTrain.updateTarget(0, .7, 0);
                else
                    opMode.driveTrain.updateTarget(0, 1, 0);

            }

        }, LINE_FOLLOWING {
            int linePosition = 0;
            int lastLinePosition = -1;

            @Override
            public void perform() {
                double motorSpeed = .1;
                if (opMode.distanceSensor.getUltrasonicLevel() < 10)
                    motorSpeed /= opMode.distanceSensor.getUltrasonicLevel();
                else if (opMode.distanceSensor.getUltrasonicLevel() <= 5)
                    nextState = BEACON_COLOR_CHOOSER;
                boolean b = opMode.opticalDistanceSensor.getRawLightDetected() > 200;
                if (b && linePosition != 0) {
                    lastLinePosition = linePosition;
                    linePosition = 0;
                } else if (!b) {
                    linePosition = (lastLinePosition + 2 % 3) - 1;
                }

                double rotPower = linePosition / 10d;
                opMode.driveTrain.updateTarget(0, motorSpeed, rotPower);
            }
        }, BEACON_COLOR_CHOOSER {
            AdafruitSensorWrapper.Colors color;
            ElapsedTime time;
            int state = 0;

            @Override
            public void perform() {
                AdafruitSensorWrapper.Colors color = opMode.colorSensor.redOrBlue();
                if (state == 0) {
                    if (color == opMode.allianceColor) {
                        opMode.colorServo.setPosition(.25);
                    } else {
                        opMode.colorServo.setPosition(.75);
                    }
                } else if (state == 1) {
                    if (time.milliseconds() > 500) {
                        opMode.colorServo.setPosition(.5);
                    } else if (time.seconds() > 1) {
                        if (opMode.colorSensor.redOrBlue() != opMode.allianceColor) {
                            state = 0;
                        } else {
                            state += 1;
                        }
                    }
                } else {
                    if (foo instanceof RobotStates) {
                        nextState = (RobotStates) foo;
                    } else {
                        nextState = GO_TO_NEXT_BEACON;
                    }
                }

            }
        },;
        //Object foo;
        RobotStates nextState;
        Auto2 opMode;
    }
}
