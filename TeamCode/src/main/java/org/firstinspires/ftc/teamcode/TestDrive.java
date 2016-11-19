package org.firstinspires.ftc.teamcode;


import com.google.common.io.Files;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.test.AdafruitSensorWrapper;
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
    private AdafruitSensorWrapper colorRight;
    private AdafruitL3GD20H gyro;
    private VuforiaLocalizer vuforia;
    private Servo buttonLeft;
    private Servo buttonRight;
    private VuforiaTrackables vuforiaTrackables;
    private OpenGLMatrix lastLocation;

    @Override
    public void init(RobotContext ctx) throws Exception {
        leftFront = hardwareMap.dcMotor("leftFront");
        leftRear = hardwareMap.dcMotor("leftRear");
        rightFront = hardwareMap.dcMotor("rightFront");
        rightRear = hardwareMap.dcMotor("rightRear");
        buttonLeft = hardwareMap.servos().get("buttonLeft");
        buttonRight = hardwareMap.servos().get("buttonRight");
       // AdafruitI2cColorSensor
        colorRight = new AdafruitSensorWrapper(hardwareMap.colorSensors().get("colorRight"), hardwareMap.deviceInterfaceModules().get("dim"), 0, false);
        colorRight.beginColorSample(25);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        gyro = new AdafruitL3GD20H(new I2cDeviceSynchImpl(hardwareMap.i2cDevices().get("gyro"), false), false);
        gyro.doInitialize();

       VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(robotControllerView().getId());
        vuforiaParams.vuforiaLicenseKey = Files.toString(new File("/sdcard/robot/vuforia.key"), Charset.forName("utf-8"));
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.createVuforiaLocalizer(vuforiaParams);
        vuforiaTrackables = vuforia.loadTrackablesFromAsset("FTC_2016-17");

        // Set names of trackables
        vuforiaTrackables.get(0).setName("Wheels");
        vuforiaTrackables.get(1).setName("Tools");
        vuforiaTrackables.get(2).setName("Legos");
        vuforiaTrackables.get(3).setName("Gears");
    }

    @Override
    public void loop(RobotContext ctx) throws Exception {
        final double robotAngle = gamepad1.leftJoystick.polar().getTheta() - Math.PI / 4;
        double rightY = gamepad1.rightJoystick.X();
        //final double r = coordinates.getR();
        double r = gamepad1.leftJoystick.polar().getR();
        final double v1 = r * Math.cos(robotAngle) + rightY;
        final double v2 = r * Math.sin(robotAngle) - rightY;
        final double v3 = r * Math.sin(robotAngle) + rightY;
        final double v4 = r * Math.cos(robotAngle) - rightY;

        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftRear.setPower(v3);
        rightRear.setPower(v4);

        if (gamepad1.isXPressed())
            buttonLeft.setPosition(1);
        if (gamepad1.isYPressed())
            buttonLeft.setPosition(0);
        if (gamepad1.isAPressed())
            buttonRight.setPosition(0);
        if (gamepad1.isBPressed())
            buttonRight.setPosition(1);

        for (VuforiaTrackable trackable : vuforiaTrackables) {
            VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) trackable.getListener();
            telemetry.data(trackable.getName(), listener.isVisible());
            if (listener.getUpdatedRobotLocation() != null)
                lastLocation = listener.getUpdatedRobotLocation();
        }
         
        telemetry.data("POS", lastLocation != null ? lastLocation.formatAsTransform() : "Unknown");


        telemetry.data("COLOR", colorRight.redOrBlue().toString());
        telemetry.data("GYRO_X", gyro.readX());
        telemetry.data("GYRO_Y", gyro.readY());
        telemetry.data("GYRO_Z", gyro.readZ());
    }

    @Override
    public void stop(RobotContext ctx, LinkedList list) {

    }

}
