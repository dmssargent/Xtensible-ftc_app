package org.firstinspires.ftc.teamcode;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.clutchauto.nav.Navigation;
import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.interfaces.RobotAction;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.EnumSet;

@Autonomous
public class Auto2 extends SimpleOpMode {
    private Navigation navigation;
    private ClutchHardware hardware;
    private EnumSet<RobotStates> states;
    private RobotStates currentState;

    private AdafruitSensorWrapper.Colors allianceColor;
    private JoystickQuestions questions;
    private RobotStates parkingLocation;

    @Override
    public void init(RobotContext ctx) throws Exception {
        // Obtain navaigtion, however this may be pointless
        // navigation = new Navigation(ctx.appContext());
        // Set hardware
        hardware = new ClutchHardware(this);

        states = EnumSet.allOf(RobotStates.class);
        questions = new JoystickQuestions(this);
        questions.addQuestion("COLOR", "What is the alliance color?", "RED", "BLUE");
    }

    @Override
    public void initLoop(@NonNull RobotContext ctx) {
        questions.loop();
    }

    @Override
    public void start(RobotContext ctx) {
        questions.stop();
        String color = questions.responseTo("COLOR");
        if (color != null && color.equals("RED")) {
            allianceColor = AdafruitSensorWrapper.Colors.RED;
        } else {
            allianceColor = AdafruitSensorWrapper.Colors.BLUE;
        }

    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        currentState.perform();
        if (currentState.nextState != currentState) {
            currentState = currentState.nextState;
        }
    }

    private enum RobotStates implements RobotAction {
        START_DRIVING {
            private ElapsedTime time;

            @Override
            public void perform() {
                if (time == null) time = new ElapsedTime();

                if (opMode.hardware.opticalDistanceSensor.getRawLightDetected() > 200) {
                    nextState = LINE_FOLLOWING;
                } else {
                    nextState = this;
                }

                if (time.seconds() > 3)
                    opMode.hardware.driveTrain.updateTarget(0, .1, 0);
                else if (time.seconds() > 2.5)
                    opMode.hardware.driveTrain.updateTarget(0, .2, 0);
                else if (time.seconds() > 2)
                    opMode.hardware.driveTrain.updateTarget(0, .5, 0);
                else if (time.seconds() > 1)
                    opMode.hardware.driveTrain.updateTarget(0, .7, 0);
                else
                    opMode.hardware.driveTrain.updateTarget(0, 1, 0);
            }

        }, LINE_FOLLOWING {
            int linePosition = 0;
            int lastLinePosition = -1;

            @Override
            public void perform() {
                double motorSpeed = .1;
                if (opMode.hardware.distanceSensor.getUltrasonicLevel() < 10)
                    motorSpeed /= opMode.hardware.distanceSensor.getUltrasonicLevel();
                else if (opMode.hardware.distanceSensor.getUltrasonicLevel() <= 5)
                    nextState = BEACON_COLOR_CHOOSER;
                boolean b = opMode.hardware.opticalDistanceSensor.getRawLightDetected() > 200;
                if (b && linePosition != 0) {
                    lastLinePosition = linePosition;
                    linePosition = 0;
                } else if (!b) {
                    linePosition = (lastLinePosition + 2 % 3) - 1;
                }

                double rotPower = linePosition / 10d;
                opMode.hardware.driveTrain.updateTarget(0, motorSpeed, rotPower);
            }
        }, BEACON_COLOR_CHOOSER {
            ElapsedTime time;
            int state = 0;

            @Override
            public void perform() {
                if (time == null) {
                    time = new ElapsedTime();
                }

                AdafruitSensorWrapper.Colors color = opMode.hardware.colorSensor.redOrBlue();
                if (state == 0) {
                    if (color == opMode.allianceColor) {
                        opMode.hardware.pressLeftBeacon();
                    } else {
                        opMode.hardware.pressRightBeacon();
                    }
                } else if (state == 1) {
                    if (time.milliseconds() > 500) {
                        opMode.hardware.pressNietherBeaconButton();
                    } else if (time.seconds() > 1) {
                        if (opMode.hardware.colorSensor.redOrBlue() != opMode.allianceColor) {
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
        }, GO_TO_NEXT_BEACON {
            ElapsedTime time;

            @Override
            public void perform() {
                if (time == null) {
                    time = new ElapsedTime();
                    opMode.hardware.driveTrain.updateTarget(0, -.1, 0);
                }

                if (time.seconds() > 2) {
                    if (opMode.hardware.opticalDistanceSensor.getRawLightDetected() > 500) {
                        foo = opMode.parkingLocation;
                        nextState = LINE_FOLLOWING;
                    }
                } else if (time.milliseconds() > 500) {
                    opMode.hardware.driveTrain.updateTarget(0, -.1, 0);
                }
            }
        };
        Object foo;
        RobotStates nextState;
        Auto2 opMode;
    }
}
