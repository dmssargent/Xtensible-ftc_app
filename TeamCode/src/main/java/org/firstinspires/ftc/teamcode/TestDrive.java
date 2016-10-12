package org.firstinspires.ftc.teamcode;


import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.ftccommunity.ftcxtensible.xsimplify.SimpleOpMode;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;


@TeleOp
public class TestDrive extends SimpleOpMode {
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftRear;
    private DcMotor rightRear;

    private ColorSensor colorLeft;
    private ColorSensor colorRight;

    private VuforiaLocalizer vuforia;

    @Override
    public void init(RobotContext ctx) throws Exception {
        leftFront = hardwareMap.dcMotor("leftFront");
        leftRear = hardwareMap.dcMotor("leftRear");
        rightFront = hardwareMap.dcMotor("rightFront");
        rightRear = hardwareMap.dcMotor("rightRear");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(robotControllerView().getId());
        vuforiaParams.vuforiaLicenseKey = Files.toString(new File("/sdcard/robot/vuforia.key"), Charset.forName("utf-8"));
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.createVuforiaLocalizer(vuforiaParams);
        VuforiaTrackables vuforiaTrackables = vuforia.loadTrackablesFromAsset("FTC_2016-17");

        // Set names of trackables
        vuforiaTrackables.get(0).setName("Wheels");
        vuforiaTrackables.get(1).setName("Tools");
        vuforiaTrackables.get(2).setName("Legos");
        vuforiaTrackables.get(3).setName("Gears");

        //
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {

        // todO: fix direction of sideways
        final double robotAngle = gamepad1.leftJoystick.polar().getTheta() - Math.PI / 4;
        double rightY = gamepad1.rightJoystick.X();
        //final double r = coordinates.getR();
        double r = gamepad1.leftJoystick.polar().getR();
        final double v1 = .54 * r * Math.cos(robotAngle) + rightY;
        final double v2 = .54 * r * Math.sin(robotAngle) - rightY;
        final double v3 = r * Math.sin(robotAngle) + rightY;
        final double v4 = r * Math.cos(robotAngle) - rightY;

        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftRear.setPower(v3);
        rightRear.setPower(v4);
    }

    @Override
    public void stop(RobotContext ctx, LinkedList list) {

    }

}
