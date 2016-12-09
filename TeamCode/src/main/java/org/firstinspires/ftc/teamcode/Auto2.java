package org.firstinspires.ftc.teamcode;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.util.EnumSet;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;
import static org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper.Colors.BLUE;
import static org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper.Colors.RED;

@Autonomous
public class Auto2 extends SimpleOpMode {
    //    private Navigation navigation;
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
        parkingLocation = RobotStates.NULL_LOCATION;
        currentState = RobotStates.START_DRIVING;

        for (RobotStates state : states) {
            state.opMode = this;
        }
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
            allianceColor = RED;
        } else {
            allianceColor = BLUE;
        }

        telemetry.addData("COLOR", allianceColor.toString()).setRetained(true);
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        currentState.perform();
        if (currentState.nextState != currentState) {
            currentState.reset();
            currentState = currentState.nextState;
        }

        telemetry.addData("CURR_STATE", currentState.toString());
    }

    private enum RobotStates implements AutoRobotAction {
        START_DRIVING {
            private ElapsedTime time;

            @Override
            public void perform() {
                if (time == null) time = new ElapsedTime();
                nextState = this;
                double rawLightDetected = opMode.hardware.opticalDistanceSensor.getRawLightDetected();
                opMode.telemetry.addData("ODS_DATA", rawLightDetected);
                if (rawLightDetected > .70) {
                    nextState = LINE_FOLLOWING;
                } else {
                    nextState = this;
                }

                if (time.seconds() > 2.5)
                    opMode.hardware.driveTrain.updateTarget(0, .18, 0);
                else if (time.seconds() > 2)
                    opMode.hardware.driveTrain.updateTarget(0, .5, 0);
                else if (time.seconds() > 1)
                    opMode.hardware.driveTrain.updateTarget(0, .7, 0);
                else
                    opMode.hardware.driveTrain.updateTarget(0, 1, 0);
            }

            @Override
            public void reset() {
                time = null;
            }
        }, LINE_FOLLOWING {
            int linePosition = -1;
            int lastLinePosition = 1;

            @Override
            public void perform() {
                nextState = this;
                double motorSpeed = .20;
                double distance = opMode.hardware.distanceSensor.getDistance(CM);
                if (distance < 10)
                    motorSpeed /= distance;
                else if (distance <= 4)
                    nextState = BEACON_COLOR_CHOOSER;
                boolean b = opMode.hardware.opticalDistanceSensor.getRawLightDetected() > .5;
                if (b && linePosition != 0) {
                    lastLinePosition = linePosition;
                    linePosition = 0;
                } else if (!b) {
                    linePosition = (lastLinePosition + 2 % 3) - 1;
                }

                opMode.telemetry.addData("LINE_POS", linePosition);

                double rotPower = linePosition / 2d;
                opMode.telemetry.addData("ROT", rotPower);
                opMode.hardware.driveTrain.updateTarget(0, motorSpeed, rotPower);
            }

            @Override
            public void reset() {
                linePosition = 0;
                lastLinePosition = -1;
            }
        }, BEACON_COLOR_CHOOSER {
            ElapsedTime time;
            int state = 0;

            @Override
            public void perform() {
                if (time == null) {
                    time = new ElapsedTime();
                }
                nextState = this;

                AdafruitSensorWrapper.Colors color = opMode.hardware.colorSensor.red() > opMode.hardware.colorSensor.blue() ?
                        RED : BLUE;
                if (state == 0) {
                    if (color == opMode.allianceColor) {
                        opMode.hardware.pressLeftBeacon();
                    } else {
                        opMode.hardware.pressRightBeacon();
                    }
                } else if (state == 1) {
                    if (time.milliseconds() > 800) {
                        opMode.hardware.pressNietherBeaconButton();
                    } else if (time.seconds() > 2) {

                        if (color != opMode.allianceColor) {
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

            public void reset() {
                time = null;
                state = 0;
            }
        }, GO_TO_NEXT_BEACON {
            ElapsedTime time;

            @Override
            public void perform() {
                if (time == null) {
                    time = new ElapsedTime();
                    opMode.hardware.driveTrain.updateTarget(0, -.1, 0);
                }
                nextState = this;
                if (time.seconds() > 2) {
                    if (opMode.hardware.opticalDistanceSensor.getRawLightDetected() > .70) {
                        foo = opMode.parkingLocation;
                        nextState = LINE_FOLLOWING;
                    }
                } else if (time.milliseconds() > 500) {
                    opMode.hardware.driveTrain.updateTarget(0, -.1, 0);
                }
            }

            @Override
            public void reset() {
                time = null;
            }
        }, NULL_LOCATION {
            @Override
            public void perform() {
                nextState = this;
            }

            @Override
            public void reset() {

            }
        };
        Object foo;
        RobotStates nextState;
        Auto2 opMode;
    }
}
