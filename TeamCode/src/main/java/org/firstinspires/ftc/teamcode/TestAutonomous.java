package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;


public class TestAutonomous extends SimpleOpMode {
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftRear;
    private DcMotor rightRear;

    private ColorSensor colorLeft;
    private AdafruitSensorWrapper colorRight;
    private AdafruitL3GD20H gyro;
    private Servo buttonLeft;
    private Servo buttonRight;
    private VuforiaTrackables vuforiaTrackables;
    private OpenGLMatrix lastLocation;

    @Override
    public void init(RobotContext ctx) throws Exception {

    }

    @Override
    public void loop(RobotContext ctx) throws Exception {

    }
}
